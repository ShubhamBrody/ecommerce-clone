package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repositories.CartItemRepository;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.ProductRepository;
import com.ecommerce.project.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<CartDTO> getAllCartDTO() {
        List<Cart> carts = cartRepository.findAll();

        if(carts.isEmpty()) {
            throw new APIException("No carts exists");
        }

        List<CartDTO> cartDTOs = carts.stream()
                .map(this::getCartDTO).toList();

        return cartDTOs;
    }

    @Override
    public CartDTO getCartDTOByUser(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if(cart == null) {
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }
        return getCartDTO(cart);
    }

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart = getOrCreateCart();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product",  "id", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(
                cart.getCartId(),
                productId
        );

        if (cartItem != null) {
            throw new APIException("Product " + product.getProductName() + " is already in cart");
        }

        if (product.getQuantity() == 0){
            throw new APIException("Product " + product.getProductName() + " is not available");
        }

        if (product.getQuantity() < quantity){
            throw new APIException("Please, make an order of the " + product.getProductName() + " less than or equal to "
                    + product.getQuantity() + ".");
        }

        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setProductPrice(product.getSpecialPrice());
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setCart(cart);
        cartItemRepository.save(newCartItem);

        //product.setQuantity(product.getQuantity());
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        cart.getCartItems().add(newCartItem);
        cartRepository.save(cart);


        List<CartItem> cartItems = cart.getCartItems();
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item -> {
            ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
            productDTO.setQuantity(item.getQuantity());
            return productDTO;
        });

        cartDTO.setProducts(productDTOStream.toList());

        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantityDelta) {

        String email = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(email);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product", "productId", productId));

        CartItem cartItem = cartItemRepository
                .findCartItemByProductIdAndCartId(cart.getCartId(), productId);

        int newQuantity = cartItem.getQuantity() + quantityDelta;

        if (newQuantity < 0) {
            throw new APIException("Quantity cannot be negative");
        }

        if (newQuantity == 0) {
            cart.getCartItems().remove(cartItem);
            cartItem.setCart(null);
        } else {
            if (quantityDelta > 0 && product.getQuantity() < newQuantity) {
                throw new APIException("Only " + product.getQuantity() + " items left in stock " + newQuantity);
            }
            cartItem.setQuantity(newQuantity);
            cartItem.setProductPrice(product.getSpecialPrice());
        }

        recalculateCartTotal(cart);
        cartRepository.save(cart);

        return getCartDTO(cart);
    }

    @Transactional
    @Override
    public String deleteProductInCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        cart.setTotalPrice(cart.getTotalPrice() -
                (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);

        return "Product " + cartItem.getProduct().getProductName() + " removed from the cart !!!";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " is not available");
        }

        double cartPrice = cart.getTotalPrice() -
                (cartItem.getProductPrice() * cartItem.getQuantity());
        cartItem.setProductPrice(product.getSpecialPrice());
        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItem = cartItemRepository.save(cartItem);
    }

    private void recalculateCartTotal(Cart cart) {
        double total = cart.getCartItems().stream()
                .mapToDouble(item -> item.getProductPrice() * item.getQuantity())
                .sum();
        cart.setTotalPrice(total);
    }

    private CartDTO getCartDTO(Cart cart) {
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cart.getCartItems().forEach(cartItem ->
                cartItem.getProduct().setQuantity(cartItem.getQuantity()));
        List<ProductDTO> products = cart.getCartItems().stream()
                .map(cartItem ->
                        modelMapper.map(cartItem.getProduct(), ProductDTO.class)).toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }

    private Cart getOrCreateCart() {
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart == null){
            userCart = new Cart();
            userCart.setTotalPrice(0.00);
            userCart.setUser(authUtil.loggedInUser());
        }
        return cartRepository.save(userCart);
    }
}
