package com.ecommerce.project.payload;

import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.OrderItem;
import com.ecommerce.project.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {
    private Long addressId;
    private String paymentMethod;
    private String pgName;
    private String pgPaymentId;
    private String pgStatus;
    private String pgResponseMessage;
}
