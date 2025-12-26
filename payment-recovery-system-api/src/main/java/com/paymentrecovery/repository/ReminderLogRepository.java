package com.paymentrecovery.repository;

import com.paymentrecovery.model.entity.ReminderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ReminderLog entity
 * Provides data access methods for reminder logs
 */
@Repository
public interface ReminderLogRepository extends JpaRepository<ReminderLog, Long> {

    /**
     * Find all reminder logs for a specific invoice
     *
     * @param invoiceId Invoice ID
     * @return List of reminder logs for the invoice
     */
    List<ReminderLog> findByInvoiceId(Long invoiceId);

    /**
     * Find reminder logs by invoice ID, ordered by sent date descending
     *
     * @param invoiceId Invoice ID
     * @return List of reminder logs ordered by sent date
     */
    @Query("SELECT rl FROM ReminderLog rl WHERE rl.invoiceId = :invoiceId ORDER BY rl.sentDate DESC")
    List<ReminderLog> findByInvoiceIdOrderBySentDateDesc(@Param("invoiceId") Long invoiceId);
}

