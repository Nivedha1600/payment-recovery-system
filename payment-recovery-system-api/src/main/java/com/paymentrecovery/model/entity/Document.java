package com.paymentrecovery.model.entity;

import com.paymentrecovery.model.enums.DocumentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Document entity - Represents uploaded invoice files
 * Documents belong to a company and optionally to an invoice
 */
@Entity
@Table(name = "documents", indexes = {
    @Index(name = "idx_document_company_id", columnList = "company_id"),
    @Index(name = "idx_document_invoice_id", columnList = "invoice_id"),
    @Index(name = "idx_document_type", columnList = "document_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(name = "fk_document_company"))
    @NotNull(message = "Company is required")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = true, foreignKey = @ForeignKey(name = "fk_document_invoice"))
    private Invoice invoice;

    @Column(name = "original_file_name", nullable = false, length = 255)
    @NotBlank(message = "Original file name is required")
    private String originalFileName;

    @Column(name = "stored_file_name", nullable = false, length = 255)
    @NotBlank(message = "Stored file name is required")
    private String storedFileName;

    @Column(name = "file_path", nullable = false, length = 500)
    @NotBlank(message = "File path is required")
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 20)
    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "file_size", nullable = false)
    @NotNull(message = "File size is required")
    private Long fileSize;

    @Column(name = "description", length = 500)
    private String description;
}

