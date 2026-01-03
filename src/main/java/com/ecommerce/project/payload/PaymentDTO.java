package com.ecommerce.project.payload;

import com.ecommerce.project.model.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private Long paymentId;
    private String paymentMethod;
    private String pgPaymentId;
    private String pgName;
    private String pgResponseMessage;
    private String pgStatus;
}
