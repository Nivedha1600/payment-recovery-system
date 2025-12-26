package com.paymentrecovery.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentrecovery.model.dto.request.ExtractedInvoiceDataRequest;
import com.paymentrecovery.model.dto.response.InvoiceReminderDto;
import com.paymentrecovery.model.entity.Company;
import com.paymentrecovery.model.entity.Customer;
import com.paymentrecovery.model.entity.Invoice;
import com.paymentrecovery.model.enums.InvoiceStatus;
import com.paymentrecovery.repository.CompanyRepository;
import com.paymentrecovery.repository.CustomerRepository;
import com.paymentrecovery.repository.InvoiceRepository;
import com.paymentrecovery.service.InvoiceExtractionService;
import com.paymentrecovery.util.InvoiceFileStorageUtil;
import com.paymentrecovery.util.InvoiceFileTypeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Invoice business logic
 * Handles invoice operations and data transformation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final InvoiceFileStorageUtil fileStorageUtil;
    private final InvoiceFileTypeValidator fileTypeValidator;
    private final InvoiceExtractionService extractionService;
    private final ObjectMapper objectMapper;

    /**
     * Upload invoice file and create DRAFT invoice
     *
     * @param file Uploaded file
     * @param companyId Company ID
     * @param customerId Optional customer ID
     * @return Created invoice ID
     * @throws IOException if file storage fails
     */
    @Transactional
    public Long uploadInvoiceFile(MultipartFile file, Long companyId, Long customerId) throws IOException {
        log.info("Uploading invoice file: {} for company: {}", file.getOriginalFilename(), companyId);

        // Validate file type
        if (!fileTypeValidator.isValidFileType(file)) {
            throw new IllegalArgumentException(
                    "File type not allowed. Supported types: " + fileTypeValidator.getAllowedFileTypes()
            );
        }

        // Validate company exists
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Company not found with ID: " + companyId));

        // Validate customer if provided
        Customer customer = null;
        if (customerId != null) {
            customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                            "Customer not found with ID: " + customerId));
            
            // Verify customer belongs to company
            if (!customer.getCompany().getId().equals(companyId)) {
                throw new IllegalArgumentException("Customer does not belong to the specified company");
            }
        }

        // Store file
        String filePath = fileStorageUtil.storeFile(file, companyId);

        // Create DRAFT invoice
        Invoice invoice = new Invoice();
        invoice.setCompany(company);
        invoice.setCustomer(customer);
        invoice.setFilePath(filePath);
        invoice.setStatus(InvoiceStatus.DRAFT);

        // Save invoice
        Invoice savedInvoice = invoiceRepository.save(invoice);

        log.info("DRAFT invoice created with ID: {} for file: {}", savedInvoice.getId(), filePath);

        // Trigger async extraction (fire-and-forget)
        try {
            extractionService.triggerExtraction(savedInvoice.getId(), filePath);
            log.debug("Triggered async extraction for invoice ID: {}", savedInvoice.getId());
        } catch (Exception e) {
            // Log but don't fail the upload - extraction is async
            log.warn("Failed to trigger extraction for invoice ID: {}, will retry later", savedInvoice.getId(), e);
        }

        return savedInvoice.getId();
    }

    /**
     * Find all invoices with PENDING status
     * Returns minimal fields required for reminders
     *
     * @return List of InvoiceReminderDto containing minimal reminder data
     */
    @Transactional(readOnly = true)
    public List<InvoiceReminderDto> findAllPendingInvoicesForReminders() {
        log.debug("Finding all pending invoices for reminders");
        
        List<Invoice> pendingInvoices = invoiceRepository
                .findAllByStatusWithCustomerAndCompany(InvoiceStatus.PENDING);
        
        log.info("Found {} pending invoices for reminders", pendingInvoices.size());
        
        return pendingInvoices.stream()
                .map(this::mapToReminderDto)
                .collect(Collectors.toList());
    }

    /**
     * Find all pending invoices for a specific company
     * Useful for multi-tenant scenarios
     *
     * @param companyId Company ID
     * @return List of InvoiceReminderDto containing minimal reminder data
     */
    @Transactional(readOnly = true)
    public List<InvoiceReminderDto> findAllPendingInvoicesForRemindersByCompany(Long companyId) {
        log.debug("Finding pending invoices for company ID: {}", companyId);
        
        List<Invoice> pendingInvoices = invoiceRepository
                .findAllByCompanyIdAndStatusWithCustomerAndCompany(companyId, InvoiceStatus.PENDING);
        
        log.info("Found {} pending invoices for company ID: {}", pendingInvoices.size(), companyId);
        
        return pendingInvoices.stream()
                .map(this::mapToReminderDto)
                .collect(Collectors.toList());
    }

    /**
     * Store extracted invoice data from Python service
     * Stores data in JSON column, does NOT activate invoice (stays DRAFT)
     *
     * @param invoiceId Invoice ID
     * @param extractedData Extracted invoice data from Python
     * @return Updated Invoice entity
     * @throws jakarta.persistence.EntityNotFoundException if invoice not found
     */
    @Transactional
    public Invoice storeExtractedData(Long invoiceId, ExtractedInvoiceDataRequest extractedData) {
        log.info("Storing extracted data for invoice ID: {}", invoiceId);

        // Fetch invoice
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> {
                    log.error("Invoice not found with ID: {}", invoiceId);
                    return new jakarta.persistence.EntityNotFoundException(
                            "Invoice not found with ID: " + invoiceId);
                });

        // Verify invoice is in DRAFT status
        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            log.warn("Invoice ID: {} is not in DRAFT status, current status: {}", 
                    invoiceId, invoice.getStatus());
            throw new IllegalStateException(
                    "Can only store extracted data for DRAFT invoices. Current status: " + invoice.getStatus());
        }

        // Convert extracted data to JSON
        try {
            JsonNode extractedDataJson = objectMapper.valueToTree(extractedData);
            invoice.setExtractedData(extractedDataJson);
            
            log.info("Stored extracted data for invoice ID: {}. Invoice remains in DRAFT status.", invoiceId);
        } catch (Exception e) {
            log.error("Error converting extracted data to JSON for invoice ID: {}", invoiceId, e);
            throw new RuntimeException("Failed to store extracted data", e);
        }

        // Save invoice (status remains DRAFT)
        Invoice updatedInvoice = invoiceRepository.save(invoice);

        log.info("Successfully stored extracted data for invoice ID: {}", invoiceId);

        return updatedInvoice;
    }

    /**
     * Maps Invoice entity to InvoiceReminderDto
     * Extracts only the fields needed for reminders
     *
     * @param invoice Invoice entity
     * @return InvoiceReminderDto with minimal reminder data
     */
    private InvoiceReminderDto mapToReminderDto(Invoice invoice) {
        return InvoiceReminderDto.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .dueDate(invoice.getDueDate())
                .amount(invoice.getAmount())
                .companyId(invoice.getCompany().getId())
                .customerName(invoice.getCustomer() != null ? invoice.getCustomer().getCustomerName() : null)
                .customerEmail(invoice.getCustomer() != null ? invoice.getCustomer().getEmail() : null)
                .customerPhone(invoice.getCustomer() != null ? invoice.getCustomer().getPhone() : null)
                .build();
    }
}

