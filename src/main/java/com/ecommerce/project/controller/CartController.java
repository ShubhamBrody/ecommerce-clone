package com.ecommerce.project.controller;

import com.ecommerce.project.model.Cart;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.service.CartService;
import com.ecommerce.project.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable("productId") Long productId,
                                              @PathVariable("quantity") Integer quantity) {
        CartDTO savedCartDTO = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<CartDTO>(savedCartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getAllCartDTO() {
        List<CartDTO> savedCartDTO = cartService.getAllCartDTO();
        return ResponseEntity.ok().body(savedCartDTO);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getAllCartDTOByUser() {
        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(emailId);
        Long cartId = cart.getCartId();
        CartDTO userCartDTO = cartService.getCartDTOByUser(emailId, cartId);
        return ResponseEntity.ok().body(userCartDTO);
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable("productId") Long productId,
                                                     @PathVariable("operation") String operation) {
        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId,
                operation.equalsIgnoreCase("delete") ? -1 : 1);

        return ResponseEntity.ok().body(cartDTO);
    }

    @DeleteMapping("carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteCartProduct(@PathVariable("cartId") Long cartId,
                                                     @PathVariable("productId") Long productId) {
        String status = cartService.deleteProductInCart(cartId, productId);
        return new ResponseEntity<String>(status, HttpStatus.OK);    }
}
