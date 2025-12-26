package com.paymentrecovery.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payment entity - Represents a payment received for an invoice
 * Payments are manually entered and belong to an invoice
 */
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_invoice_id", columnList = "invoice_id"),
    @Index(name = "idx_payment_payment_date", columnList = "payment_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false, foreignKey = @ForeignKey(name = "fk_payment_invoice"))
    @NotNull(message = "Invoice is required")
    private Invoice invoice;

    @Column(name = "amount_received", nullable = false, precision = 19, scale = 2)
    @NotNull(message = "Amount received is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount received must be greater than 0")
    private BigDecimal amountReceived;

    @Column(name = "payment_date", nullable = false)
    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;
}

