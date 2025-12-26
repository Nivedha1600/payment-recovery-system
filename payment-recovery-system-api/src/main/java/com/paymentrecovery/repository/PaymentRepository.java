package com.paymentrecovery.repository;

import com.paymentrecovery.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Payment entity
 * Provides data access methods for payments
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find all payments for a specific invoice
     *
     * @param invoiceId Invoice ID
     * @return List of payments for the invoice
     */
    List<Payment> findByInvoiceId(Long invoiceId);

    /**
     * Find all payments for an invoice, ordered by payment date descending
     *
     * @param invoiceId Invoice ID
     * @return List of payments ordered by payment date
     */
    @Query("SELECT p FROM Payment p WHERE p.invoice.id = :invoiceId ORDER BY p.paymentDate DESC")
    List<Payment> findByInvoiceIdOrderByPaymentDateDesc(@Param("invoiceId") Long invoiceId);

    /**
     * Calculate total amount received for an invoice
     *
     * @param invoiceId Invoice ID
     * @return Total amount received
     */
    @Query("SELECT COALESCE(SUM(p.amountReceived), 0) FROM Payment p WHERE p.invoice.id = :invoiceId")
    java.math.BigDecimal getTotalAmountReceivedByInvoiceId(@Param("invoiceId") Long invoiceId);
}

