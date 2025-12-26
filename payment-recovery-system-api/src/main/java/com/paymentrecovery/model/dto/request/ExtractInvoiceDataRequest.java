package com.paymentrecovery.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for invoice data extraction
 * Sent to Python service for data extraction
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtractInvoiceDataRequest {

    private Long invoiceId;
    private String filePath;
}

