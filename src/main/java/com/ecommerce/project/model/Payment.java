package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.ast.Or;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne(mappedBy = "payment", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Order order;

    @NotBlank
    @Size(min = 1, message = "Payment method must contain at least 4 characters")
    private String paymentMethod;

    private String pgPaymentId;
    private String pgName;
    private String pgResponseMessage;
    private String pgStatus;

    public Payment(String paymentMethod,
                   String pgPaymentId,
                   String pgName,
                   String pgResponseMessage,
                   String pgStatus) {
        this.paymentMethod = paymentMethod;
        this.pgPaymentId = pgPaymentId;
        this.pgName = pgName;
        this.pgResponseMessage = pgResponseMessage;
        this.pgStatus = pgStatus;
    }
}
