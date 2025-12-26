package com.paymentrecovery.repository;

import com.paymentrecovery.model.entity.Document;
import com.paymentrecovery.model.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Document entity
 * Provides data access methods for documents
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    /**
     * Find all documents for a specific company
     *
     * @param companyId Company ID
     * @return List of documents for the company
     */
    List<Document> findByCompanyId(Long companyId);

    /**
     * Find all documents for a specific invoice
     *
     * @param invoiceId Invoice ID
     * @return List of documents for the invoice
     */
    List<Document> findByInvoiceId(Long invoiceId);

    /**
     * Find documents by company and invoice
     *
     * @param companyId Company ID
     * @param invoiceId Invoice ID
     * @return List of documents
     */
    List<Document> findByCompanyIdAndInvoiceId(Long companyId, Long invoiceId);

    /**
     * Find documents by type
     *
     * @param documentType Document type
     * @return List of documents
     */
    List<Document> findByDocumentType(DocumentType documentType);

    /**
     * Find document by stored file name
     *
     * @param storedFileName Stored file name
     * @return Optional document
     */
    Optional<Document> findByStoredFileName(String storedFileName);
}

