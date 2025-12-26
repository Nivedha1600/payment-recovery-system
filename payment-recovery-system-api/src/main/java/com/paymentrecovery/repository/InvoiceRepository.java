package com.paymentrecovery.repository;

import com.paymentrecovery.model.entity.Invoice;
import com.paymentrecovery.model.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Invoice entity
 * Provides data access methods for invoices
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    /**
     * Find all invoices with PENDING status
     * Eagerly fetches customer and company to avoid N+1 queries
     * Note: Uses LEFT JOIN for customer as it can be null for DRAFT invoices
     *
     * @return List of invoices with PENDING status
     */
    @Query("SELECT i FROM Invoice i " +
           "LEFT JOIN FETCH i.customer c " +
           "JOIN FETCH i.company " +
           "WHERE i.status = :status " +
           "ORDER BY i.dueDate ASC")
    List<Invoice> findAllByStatusWithCustomerAndCompany(@Param("status") InvoiceStatus status);

    /**
     * Find all invoices with PENDING status for a specific company
     * Useful for multi-tenant queries
     * Note: Uses LEFT JOIN for customer as it can be null for DRAFT invoices
     *
     * @param companyId Company ID
     * @param status Invoice status
     * @return List of invoices with specified status for the company
     */
    @Query("SELECT i FROM Invoice i " +
           "LEFT JOIN FETCH i.customer c " +
           "JOIN FETCH i.company " +
           "WHERE i.company.id = :companyId AND i.status = :status " +
           "ORDER BY i.dueDate ASC")
    List<Invoice> findAllByCompanyIdAndStatusWithCustomerAndCompany(
            @Param("companyId") Long companyId,
            @Param("status") InvoiceStatus status
    );

    /**
     * Simple method to find invoices by status
     * Uses default JPA method naming convention
     *
     * @param status Invoice status
     * @return List of invoices with specified status
     */
    List<Invoice> findByStatus(InvoiceStatus status);
}

