package com.paymentrecovery.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for marking an invoice as paid
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkInvoicePaidRequest {

    @NotNull(message = "Amount received is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount received must be greater than 0")
    private BigDecimal amountReceived;

    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;
}

