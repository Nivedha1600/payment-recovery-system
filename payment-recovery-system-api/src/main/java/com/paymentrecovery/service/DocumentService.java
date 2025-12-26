package com.paymentrecovery.service;

import com.paymentrecovery.model.dto.response.DocumentResponse;
import com.paymentrecovery.model.entity.Company;
import com.paymentrecovery.model.entity.Document;
import com.paymentrecovery.model.entity.Invoice;
import com.paymentrecovery.model.enums.DocumentType;
import com.paymentrecovery.repository.CompanyRepository;
import com.paymentrecovery.repository.DocumentRepository;
import com.paymentrecovery.repository.InvoiceRepository;
import com.paymentrecovery.util.DocumentTypeDetector;
import com.paymentrecovery.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service for Document business logic
 * Handles file uploads and document management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final CompanyRepository companyRepository;
    private final InvoiceRepository invoiceRepository;
    private final FileStorageUtil fileStorageUtil;
    private final DocumentTypeDetector documentTypeDetector;

    /**
     * Upload and save document
     *
     * @param file Multipart file
     * @param companyId Company ID
     * @param invoiceId Optional invoice ID
     * @param description Optional description
     * @return DocumentResponse with saved document data
     * @throws IOException if file storage fails
     */
    @Transactional
    public DocumentResponse uploadDocument(
            MultipartFile file,
            Long companyId,
            Long invoiceId,
            String description
    ) throws IOException {
        log.info("Uploading document: {} for company: {}", file.getOriginalFilename(), companyId);

        // Validate file type
        if (!documentTypeDetector.isAllowedFileType(file)) {
            throw new IllegalArgumentException("File type not allowed. Supported: PDF, Images, DOC, Excel");
        }

        // Validate company exists
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Company not found with ID: " + companyId));

        // Validate invoice if provided
        Invoice invoice = null;
        if (invoiceId != null) {
            invoice = invoiceRepository.findById(invoiceId)
                    .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                            "Invoice not found with ID: " + invoiceId));
            
            // Verify invoice belongs to company
            if (!invoice.getCompany().getId().equals(companyId)) {
                throw new IllegalArgumentException("Invoice does not belong to the specified company");
            }
        }

        // Detect document type
        DocumentType documentType = documentTypeDetector.detectDocumentType(file);

        // Store file
        String filePath = fileStorageUtil.storeFile(file, companyId);

        // Extract stored file name from path
        String storedFileName = filePath.substring(filePath.lastIndexOf('/') + 1);

        // Create document entity
        Document document = new Document();
        document.setCompany(company);
        document.setInvoice(invoice);
        document.setOriginalFileName(file.getOriginalFilename());
        document.setStoredFileName(storedFileName);
        document.setFilePath(filePath);
        document.setDocumentType(documentType);
        document.setMimeType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setDescription(description);

        // Save to database
        Document savedDocument = documentRepository.save(document);

        log.info("Document uploaded successfully with ID: {}", savedDocument.getId());

        return mapToResponse(savedDocument);
    }

    /**
     * Maps Document entity to DocumentResponse DTO
     *
     * @param document Document entity
     * @return DocumentResponse DTO
     */
    private DocumentResponse mapToResponse(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .companyId(document.getCompany().getId())
                .invoiceId(document.getInvoice() != null ? document.getInvoice().getId() : null)
                .originalFileName(document.getOriginalFileName())
                .storedFileName(document.getStoredFileName())
                .filePath(document.getFilePath())
                .documentType(document.getDocumentType())
                .mimeType(document.getMimeType())
                .fileSize(document.getFileSize())
                .description(document.getDescription())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}

