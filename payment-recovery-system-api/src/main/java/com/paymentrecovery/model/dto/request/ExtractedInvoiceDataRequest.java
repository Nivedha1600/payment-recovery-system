package com.paymentrecovery.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Request DTO for extracted invoice data from Python service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtractedInvoiceDataRequest {

    private String invoiceNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate invoiceDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    
    private BigDecimal amount;
    
    private String customerName;
    
    private String customerEmail;
    
    private String customerPhone;
    
    private List<Map<String, Object>> lineItems;
    
    private BigDecimal taxAmount;
    
    private BigDecimal totalAmount;
    
    private String currency;
    
    private String notes;
    
    // Additional extracted fields as JSON
    private Map<String, Object> additionalData;
}

