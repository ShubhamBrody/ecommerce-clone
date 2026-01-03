package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Double totalPrice;
}
