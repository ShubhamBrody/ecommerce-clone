package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderItemDTO;
import com.ecommerce.project.repositories.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(CartRepository cartRepository, AddressRepository addressRepository, PaymentRepository paymentRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository, CartItemRepository cartItemRepository, CartService cartService, ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartService = cartService;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public OrderDTO placeOrder(String emailId,
                               String paymentMethod,
                               Long addressId,
                               String pgName,
                               String pgPaymentId,
                               String pgStatus,
                               String pgResponseMessage) {
        Cart cart = cartRepository.findCartByEmail(emailId);
        if(cart == null){
            throw new ResourceNotFoundException("Cart", "email", emailId);
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        Order order = new Order();
        order.setEmail(emailId);
        order.setAddress(address);
        order.setOrderDate(LocalDate.now());
        order.setOrderStatus("Order Accepted!");
        order.setTotalPrice(cart.getTotalPrice());

        Payment payment = new Payment(paymentMethod, pgPaymentId, pgName, pgResponseMessage, pgStatus);
        payment.setOrder(order);
        payment = paymentRepository.save(payment);

        order.setPayment(payment);
        Order savedOrder = orderRepository.save(order);

        List<CartItem> cartItems = cart.getCartItems();
        if(cartItems == null || cartItems.isEmpty()){
            throw new APIException("Cart is empty");
        }

        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setDiscount(cartItem.getDiscount());
                    orderItem.setOrderedProductPrice(cartItem.getProductPrice());
                    orderItem.setOrder(savedOrder);
                    return orderItem;
                }).toList();

        orderItems = orderItemRepository.saveAll(orderItems);

        for(CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);
            cartService.deleteProductInCart(cart.getCartId(), product.getProductId());
        }

        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        List<OrderItemDTO> orderItemDTOs = orderItems.stream()
                .map(orderItem ->
                        modelMapper.map(orderItem, OrderItemDTO.class)).toList();
        orderDTO.setOrderItems(orderItemDTOs);
        orderDTO.setAddressId(addressId);

        return orderDTO;
    }
}
