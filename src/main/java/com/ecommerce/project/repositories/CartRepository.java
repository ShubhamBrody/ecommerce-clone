package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart,Long> {
    @Query("SELECT C FROM Cart C where C.user.email = ?1")
    Cart findCartByEmail(String email);

    @Query("SELECT C FROM Cart C where C.user.email = ?1 AND C.cartId = ?2")
    Cart findCartByEmailAndCartId(String emailId, Long cartId);

    @Query("SELECT C FROM Cart C JOIN FETCH C.cartItems CI JOIN FETCH CI.product P WHERE P.productId = ?1")
    List<Cart> findCartsByProductId(Long productId);
}
