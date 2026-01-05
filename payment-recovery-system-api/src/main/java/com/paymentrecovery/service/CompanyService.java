package com.paymentrecovery.service;

import com.paymentrecovery.model.enums.InvoiceStatus;
import com.paymentrecovery.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Company Service
 * Handles business logic for company-specific operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CompanyService {

    private final InvoiceRepository invoiceRepository;

    /**
     * Get dashboard metrics for a company
     * 
     * @param companyId Company ID
     * @return Map containing dashboard metrics
     */
    public Map<String, Object> getDashboardMetrics(Long companyId) {
        log.info("Calculating dashboard metrics for company ID: {}", companyId);

        // Calculate invoice counts
        // Total invoices = PENDING + PAID (active invoices, excluding DRAFT)
        long pendingInvoices = invoiceRepository.countByCompanyIdAndStatus(companyId, InvoiceStatus.PENDING);
        long paidInvoices = invoiceRepository.countByCompanyIdAndStatus(companyId, InvoiceStatus.PAID);
        long totalInvoices = pendingInvoices + paidInvoices; // Only count active invoices (not DRAFT)

        // Calculate amounts
        BigDecimal pendingAmount = invoiceRepository.sumTotalAmountByCompanyIdAndStatus(companyId, InvoiceStatus.PENDING);
        
        LocalDate today = LocalDate.now();
        BigDecimal overdueAmount = invoiceRepository.sumOverdueAmountByCompany(companyId, InvoiceStatus.PENDING, today);
        
        // Calculate money recovered this month (paid invoices this month)
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime startOfNextMonth = startOfMonth.plusMonths(1);
        BigDecimal moneyRecoveredThisMonth = invoiceRepository.sumPaidAmountThisMonthByCompany(
                companyId, InvoiceStatus.PAID, startOfMonth, startOfNextMonth);

        long overdueInvoices = invoiceRepository.findOverdueInvoicesByCompany(companyId, InvoiceStatus.PENDING, today).size();

        // Build response
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalInvoices", totalInvoices);
        metrics.put("pendingAmount", pendingAmount != null ? pendingAmount.doubleValue() : 0.0);
        metrics.put("overdueAmount", overdueAmount != null ? overdueAmount.doubleValue() : 0.0);
        metrics.put("moneyRecoveredThisMonth", moneyRecoveredThisMonth != null ? moneyRecoveredThisMonth.doubleValue() : 0.0);
        metrics.put("pendingInvoices", pendingInvoices);
        metrics.put("paidInvoices", paidInvoices);
        metrics.put("overdueInvoices", overdueInvoices);

        log.info("Dashboard metrics calculated for company ID: {} - Total: {}, Pending: {}, Paid: {}", 
                companyId, totalInvoices, pendingInvoices, paidInvoices);

        return metrics;
    }
}

