package com.ecommerce.project.service;

import com.ecommerce.project.payload.CartDTO;

import java.util.List;

public interface CartService {
    List<CartDTO> getAllCartDTO();
    CartDTO getCartDTOByUser(String emailId, Long cartId);
    CartDTO addProductToCart(Long productId, Integer quantity);
    CartDTO updateProductQuantityInCart(Long productId, Integer quantity);
    String deleteProductInCart(Long cartId, Long productId);
    void updateProductInCarts(Long cartId, Long productId);
}
