package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Long cartItemid;
    private ProductDTO productDTO;
    private CartDTO cart;
    private Integer quantity;
    private Double discount;
    private Double productPrice;
}
