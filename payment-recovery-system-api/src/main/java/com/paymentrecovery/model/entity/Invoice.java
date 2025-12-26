package com.paymentrecovery.model.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.paymentrecovery.model.enums.InvoiceStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Invoice entity - Represents an invoice
 * Each invoice belongs to a company and a customer
 */
@Entity
@Table(name = "invoices", indexes = {
    @Index(name = "idx_invoice_company_id", columnList = "company_id"),
    @Index(name = "idx_invoice_customer_id", columnList = "customer_id"),
    @Index(name = "idx_invoice_invoice_number", columnList = "invoice_number"),
    @Index(name = "idx_invoice_status", columnList = "status"),
    @Index(name = "idx_invoice_due_date", columnList = "due_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Invoice extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(name = "fk_invoice_company"))
    @NotNull(message = "Company is required")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = true, foreignKey = @ForeignKey(name = "fk_invoice_customer"))
    private Customer customer;

    @Column(name = "invoice_number", length = 100)
    private String invoiceNumber;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extracted_data", columnDefinition = "jsonb")
    private JsonNode extractedData;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @NotNull(message = "Status is required")
    private InvoiceStatus status = InvoiceStatus.DRAFT;
}

