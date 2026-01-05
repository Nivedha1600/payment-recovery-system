package com.paymentrecovery.controller.api;

import com.paymentrecovery.model.dto.request.CreateInvoiceRequest;
import com.paymentrecovery.model.dto.request.ExtractedInvoiceDataRequest;
import com.paymentrecovery.model.dto.request.MarkInvoicePaidRequest;
import com.paymentrecovery.model.dto.response.InvoiceReminderDto;
import com.paymentrecovery.model.entity.Invoice;
import com.paymentrecovery.service.InvoiceService;
import com.paymentrecovery.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Invoice operations
 * Handles HTTP requests related to invoices
 */
@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Invoice", description = "Invoice management APIs")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final PaymentService paymentService;

    /**
     * Get all pending invoices for reminders
     * Returns minimal fields required for sending reminders
     *
     * @return List of InvoiceReminderDto
     */
    @GetMapping("/pending-for-reminder")
    @Operation(
            summary = "Get pending invoices for reminders",
            description = "Retrieves all invoices with PENDING status that need reminders. " +
                         "Returns minimal fields required for reminder processing."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved pending invoices",
                    content = @Content(schema = @Schema(implementation = InvoiceReminderDto.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<List<InvoiceReminderDto>> getPendingInvoicesForReminder() {
        log.info("Received request to get pending invoices for reminders");
        
        try {
            List<InvoiceReminderDto> pendingInvoices = invoiceService.findAllPendingInvoicesForReminders();
            
            log.info("Successfully retrieved {} pending invoices for reminders", pendingInvoices.size());
            
            return ResponseEntity.ok(pendingInvoices);
        } catch (Exception e) {
            log.error("Error retrieving pending invoices for reminders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Mark an invoice as paid
     * Creates a payment record and updates the invoice status to PAID
     *
     * @param invoiceId Invoice ID to mark as paid
     * @param request MarkInvoicePaidRequest containing payment details
     * @return Updated Invoice entity
     */
    @PostMapping("/{invoiceId}/mark-paid")
    @Operation(
            summary = "Mark invoice as paid",
            description = "Creates a payment record for the invoice and updates the invoice status to PAID. " +
                         "This operation is transactional - both payment creation and invoice update happen atomically."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Invoice marked as paid successfully",
                    content = @Content(schema = @Schema(implementation = Invoice.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Invoice not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<Invoice> markInvoiceAsPaid(
            @PathVariable Long invoiceId,
            @Valid @RequestBody MarkInvoicePaidRequest request) {
        log.info("Received request to mark invoice ID: {} as paid with amount: {}, date: {}", 
                invoiceId, request.getAmountReceived(), request.getPaymentDate());

        try {
            Invoice updatedInvoice = paymentService.markInvoiceAsPaid(invoiceId, request);
            
            log.info("Successfully marked invoice ID: {} as PAID", invoiceId);
            
            return ResponseEntity.ok(updatedInvoice);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            log.error("Invoice not found with ID: {}", invoiceId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error marking invoice ID: {} as paid", invoiceId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Upload invoice file and create DRAFT invoice
     * Accepts PDF, PNG, JPG, DOC, DOCX, XLS, XLSX files
     *
     * @param file Uploaded file
     * @param companyId Company ID (required)
     * @param customerId Optional customer ID
     * @return Invoice ID of created DRAFT invoice
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload invoice file",
            description = "Uploads an invoice file (PDF, PNG, JPG, DOC, DOCX, XLS, XLSX, CSV) " +
                         "and creates a DRAFT invoice record. File is saved to local storage. " +
                         "Data extraction is not performed."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Invoice file uploaded and DRAFT invoice created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid file type or request data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Company or Customer not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<Map<String, Object>> uploadInvoiceFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("companyId") Long companyId,
            @RequestParam(value = "customerId", required = false) Long customerId
    ) {
        log.info(
                "Received invoice file upload request: file={}, companyId={}, customerId={}",
                file.getOriginalFilename(), companyId, customerId
        );

        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Upload file and create DRAFT invoice
            Long invoiceId = invoiceService.uploadInvoiceFile(file, companyId, customerId);

            Map<String, Object> response = new HashMap<>();
            response.put("invoiceId", invoiceId);
            response.put("message", "Invoice file uploaded and DRAFT invoice created successfully");

            log.info("Invoice file uploaded successfully, DRAFT invoice ID: {}", invoiceId);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (jakarta.persistence.EntityNotFoundException e) {
            log.error("Entity not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error uploading invoice file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a DRAFT invoice manually (without file upload)
     * Used when user enters invoice data directly
     *
     * @param request CreateInvoiceRequest with invoice details
     * @param companyId Company ID (from JWT token in production, query param for now)
     * @return Invoice ID of created DRAFT invoice
     */
    @PostMapping("/create")
    @Operation(
            summary = "Create DRAFT invoice manually",
            description = "Creates a DRAFT invoice with manually entered data (no file upload). " +
                         "Invoice must be confirmed later to become active."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "DRAFT invoice created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Company or Customer not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<Map<String, Object>> createInvoice(
            @Valid @RequestBody CreateInvoiceRequest request,
            @RequestParam("companyId") Long companyId
    ) {
        log.info("Received manual invoice creation request for company: {}", companyId);

        try {
            // Create DRAFT invoice
            Long invoiceId = invoiceService.createDraftInvoice(request, companyId);

            Map<String, Object> response = new HashMap<>();
            response.put("invoiceId", invoiceId);
            response.put("message", "DRAFT invoice created successfully");

            log.info("DRAFT invoice created manually, invoice ID: {}", invoiceId);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (jakarta.persistence.EntityNotFoundException e) {
            log.error("Entity not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error creating invoice", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Store extracted invoice data from Python service
     * Stores data in JSON column, invoice remains in DRAFT status
     *
     * @param invoiceId Invoice ID
     * @param request ExtractedInvoiceDataRequest with extracted data
     * @return Updated Invoice entity
     */
    @PostMapping("/{invoiceId}/extracted-data")
    @Operation(
            summary = "Store extracted invoice data",
            description = "Receives extracted invoice data from Python service and stores it in JSON column. " +
                         "Invoice remains in DRAFT status and is not activated."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Extracted data stored successfully",
                    content = @Content(schema = @Schema(implementation = Invoice.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or invoice not in DRAFT status"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Invoice not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<Invoice> storeExtractedData(
            @PathVariable Long invoiceId,
            @Valid @RequestBody ExtractedInvoiceDataRequest request) {
        log.info("Received extracted data for invoice ID: {}", invoiceId);

        try {
            Invoice updatedInvoice = invoiceService.storeExtractedData(invoiceId, request);
            
            log.info("Successfully stored extracted data for invoice ID: {}", invoiceId);
            
            return ResponseEntity.ok(updatedInvoice);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            log.error("Invoice not found with ID: {}", invoiceId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            log.error("Invalid invoice status for ID: {} - {}", invoiceId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("Error storing extracted data for invoice ID: {}", invoiceId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Confirm a DRAFT invoice and activate it
     * Moves invoice from DRAFT to PENDING status (becomes eligible for reminders)
     *
     * @param invoiceId Invoice ID to confirm
     * @param request ConfirmInvoiceRequest with invoice details
     * @return Updated Invoice entity with PENDING status
     */
    @PostMapping("/{invoiceId}/confirm")
    @Operation(
            summary = "Confirm DRAFT invoice",
            description = "Confirms a DRAFT invoice by updating its fields and changing status to PENDING. " +
                         "Once confirmed, the invoice becomes eligible for automated reminders."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Invoice confirmed successfully",
                    content = @Content(schema = @Schema(implementation = Invoice.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or invoice not in DRAFT status"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Invoice or Customer not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<Invoice> confirmInvoice(
            @PathVariable Long invoiceId,
            @Valid @RequestBody com.paymentrecovery.model.dto.request.ConfirmInvoiceRequest request) {
        log.info("Received request to confirm invoice ID: {}", invoiceId);

        try {
            Invoice confirmedInvoice = invoiceService.confirmInvoice(invoiceId, request);
            
            log.info("Successfully confirmed invoice ID: {}", invoiceId);
            
            return ResponseEntity.ok(confirmedInvoice);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            log.error("Entity not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            log.error("Invalid invoice status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("Error confirming invoice ID: {}", invoiceId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all DRAFT invoices for review
     * Returns invoices that need confirmation before becoming active
     *
     * @param companyId Company ID (from JWT token in production)
     * @return List of DRAFT invoices
     */
    @GetMapping("/drafts")
    @Operation(
            summary = "Get DRAFT invoices",
            description = "Retrieves all DRAFT invoices for a company that need review and confirmation."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved DRAFT invoices"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<List<Invoice>> getDraftInvoices(
            @RequestParam Long companyId) {
        log.info("Received request to get DRAFT invoices for company ID: {}", companyId);

        try {
            List<Invoice> draftInvoices = invoiceService.getDraftInvoices(companyId);
            
            log.info("Successfully retrieved {} DRAFT invoices for company ID: {}", 
                    draftInvoices.size(), companyId);
            
            return ResponseEntity.ok(draftInvoices);
        } catch (Exception e) {
            log.error("Error retrieving DRAFT invoices for company ID: {}", companyId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

