package com.ecommerce.project.payload;

import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.Payment;
import com.ecommerce.project.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long orderId;
    private String email;
    private Long addressId;
    private List<OrderItemDTO> orderItems;
    private PaymentDTO payment;
    private Double totalPrice;
    private LocalDate orderDate;
    private String orderStatus;
}
