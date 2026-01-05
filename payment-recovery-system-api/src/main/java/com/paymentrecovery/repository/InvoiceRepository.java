package com.paymentrecovery.repository;

import com.paymentrecovery.model.entity.Invoice;
import com.paymentrecovery.model.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    /**
     * Count invoices by company ID
     */
    long countByCompanyId(Long companyId);

    /**
     * Count invoices by company ID and status
     */
    long countByCompanyIdAndStatus(Long companyId, InvoiceStatus status);

    /**
     * Find invoices by company ID and status
     */
    List<Invoice> findByCompanyIdAndStatus(Long companyId, InvoiceStatus status);

    /**
     * Find all invoices by company ID
     * Eagerly fetches company and customer to avoid lazy loading issues
     */
    @Query("SELECT i FROM Invoice i " +
           "LEFT JOIN FETCH i.customer c " +
           "JOIN FETCH i.company " +
           "WHERE i.company.id = :companyId " +
           "ORDER BY i.createdAt DESC")
    List<Invoice> findByCompanyId(@Param("companyId") Long companyId);

    /**
     * Find invoice by ID with eagerly fetched relationships
     * Used for invoice detail view to avoid lazy loading issues
     */
    @Query("SELECT i FROM Invoice i " +
           "LEFT JOIN FETCH i.customer c " +
           "JOIN FETCH i.company " +
           "WHERE i.id = :invoiceId")
    Invoice findByIdWithRelationships(@Param("invoiceId") Long invoiceId);

    /**
     * Find overdue invoices for a company (pending invoices with due date before today)
     */
    @Query("SELECT i FROM Invoice i WHERE i.company.id = :companyId " +
           "AND i.status = :status AND i.dueDate < :today")
    List<Invoice> findOverdueInvoicesByCompany(
            @Param("companyId") Long companyId,
            @Param("status") InvoiceStatus status,
            @Param("today") java.time.LocalDate today
    );

    /**
     * Sum total amount of invoices by company ID and status
     */
    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Invoice i " +
           "WHERE i.company.id = :companyId AND i.status = :status")
    BigDecimal sumTotalAmountByCompanyIdAndStatus(
            @Param("companyId") Long companyId,
            @Param("status") InvoiceStatus status
    );

    /**
     * Sum total amount of overdue invoices for a company
     */
    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Invoice i " +
           "WHERE i.company.id = :companyId AND i.status = :status " +
           "AND i.dueDate < :today")
    BigDecimal sumOverdueAmountByCompany(
            @Param("companyId") Long companyId,
            @Param("status") InvoiceStatus status,
            @Param("today") java.time.LocalDate today
    );

    /**
     * Sum total amount of paid invoices for a company this month
     */
    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Invoice i " +
           "WHERE i.company.id = :companyId AND i.status = :status " +
           "AND i.updatedAt >= :startDate AND i.updatedAt < :endDate")
    BigDecimal sumPaidAmountThisMonthByCompany(
            @Param("companyId") Long companyId,
            @Param("status") InvoiceStatus status,
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate
    );
}

