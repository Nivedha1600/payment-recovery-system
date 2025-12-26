package com.paymentrecovery.service;

import com.paymentrecovery.model.dto.request.MarkInvoicePaidRequest;
import com.paymentrecovery.model.entity.Invoice;
import com.paymentrecovery.model.entity.Payment;
import com.paymentrecovery.model.enums.InvoiceStatus;
import com.paymentrecovery.repository.InvoiceRepository;
import com.paymentrecovery.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for Payment business logic
 * Handles payment operations and invoice status updates
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    /**
     * Mark an invoice as paid by creating a payment record and updating invoice status
     * This operation is transactional - both payment creation and invoice update happen atomically
     *
     * @param invoiceId Invoice ID to mark as paid
     * @param request MarkInvoicePaidRequest containing payment details
     * @return Updated Invoice entity
     * @throws jakarta.persistence.EntityNotFoundException if invoice not found
     */
    @Transactional
    public Invoice markInvoiceAsPaid(Long invoiceId, MarkInvoicePaidRequest request) {
        log.debug("Marking invoice ID: {} as paid with amount: {}, date: {}", 
                invoiceId, request.getAmountReceived(), request.getPaymentDate());

        // Fetch invoice
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> {
                    log.error("Invoice not found with ID: {}", invoiceId);
                    return new jakarta.persistence.EntityNotFoundException(
                            "Invoice not found with ID: " + invoiceId);
                });

        // Create payment record
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmountReceived(request.getAmountReceived());
        payment.setPaymentDate(request.getPaymentDate());

        Payment savedPayment = paymentRepository.save(payment);
        log.debug("Created payment with ID: {} for invoice ID: {}", savedPayment.getId(), invoiceId);

        // Update invoice status to PAID
        invoice.setStatus(InvoiceStatus.PAID);
        Invoice updatedInvoice = invoiceRepository.save(invoice);

        log.info("Successfully marked invoice ID: {} as PAID. Payment ID: {}", 
                invoiceId, savedPayment.getId());

        return updatedInvoice;
    }
}

