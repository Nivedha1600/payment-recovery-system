package com.paymentrecovery.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for invoice reminder data
 * Contains minimal fields required for sending reminders
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceReminderDto {

    private Long id;
    private String invoiceNumber;
    private LocalDate dueDate;
    private BigDecimal amount;
    private Long companyId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
}

