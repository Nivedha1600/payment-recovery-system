package com.paymentrecovery.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for confirming a DRAFT invoice
 * Moves invoice from DRAFT to PENDING (ACTIVE) status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmInvoiceRequest {
    
    @NotNull(message = "Invoice number is required")
    private String invoiceNumber;
    
    @NotNull(message = "Invoice date is required")
    private LocalDate invoiceDate;
    
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;
    
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    
    private Long customerId; // Optional - can be set from extracted data or manually
}

