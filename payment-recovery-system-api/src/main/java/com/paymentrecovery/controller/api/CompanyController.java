package com.paymentrecovery.controller.api;

import com.paymentrecovery.model.dto.request.MarkInvoicePaidRequest;
import com.paymentrecovery.model.dto.request.UpdateCompanyProfileRequest;
import com.paymentrecovery.model.entity.Company;
import com.paymentrecovery.model.entity.Invoice;
import com.paymentrecovery.model.enums.InvoiceStatus;
import com.paymentrecovery.repository.CompanyRepository;
import com.paymentrecovery.repository.InvoiceRepository;
import com.paymentrecovery.security.jwt.JwtTokenProvider;
import com.paymentrecovery.service.CompanyService;
import com.paymentrecovery.service.PaymentService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Company Controller
 * Handles company-specific endpoints for authenticated company users
 */
@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Company", description = "Company-specific APIs")
public class CompanyController {

    private final CompanyService companyService;
    private final InvoiceRepository invoiceRepository;
    private final CompanyRepository companyRepository;
    private final PaymentService paymentService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Get company dashboard metrics
     * Returns metrics specific to the logged-in company
     */
    @GetMapping("/dashboard/metrics")
    @Operation(
            summary = "Get company dashboard metrics",
            description = "Retrieves dashboard metrics for the authenticated company including " +
                         "total invoices, pending amounts, overdue amounts, and money recovered this month."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved dashboard metrics",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing token"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<Map<String, Object>> getDashboardMetrics(HttpServletRequest request) {
        log.info("Received request for company dashboard metrics");

        try {
            // Get company ID from JWT token
            String token = getTokenFromRequest(request);
            if (token == null) {
                log.warn("No token found in request");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Long companyId = jwtTokenProvider.getCompanyIdFromToken(token);
            if (companyId == null) {
                log.warn("No company ID found in token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Map<String, Object> metrics = companyService.getDashboardMetrics(companyId);
            
            log.info("Successfully retrieved dashboard metrics for company ID: {}", companyId);
            return ResponseEntity.ok(metrics);
            
        } catch (Exception e) {
            log.error("Error fetching dashboard metrics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get invoices for the logged-in company
     * Supports pagination and status filtering
     */
    @GetMapping("/invoices")
    @Operation(
            summary = "Get company invoices",
            description = "Retrieves invoices for the authenticated company with pagination and optional status filter."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved invoices"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing token"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<Map<String, Object>> getInvoices(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search
    ) {
        log.info("Received request for company invoices - page: {}, size: {}, status: {}", page, size, status);

        try {
            // Get company ID from JWT token
            String token = getTokenFromRequest(request);
            if (token == null) {
                log.warn("No token found in request");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Long companyId = jwtTokenProvider.getCompanyIdFromToken(token);
            if (companyId == null) {
                log.warn("No company ID found in token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Build pageable
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

            // Get invoices based on status filter
            List<Invoice> invoices;
            long total;

            if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("ALL")) {
                try {
                    InvoiceStatus invoiceStatus = InvoiceStatus.valueOf(status.toUpperCase());
                    invoices = invoiceRepository.findByCompanyIdAndStatus(companyId, invoiceStatus);
                    total = invoiceRepository.countByCompanyIdAndStatus(companyId, invoiceStatus);
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid status filter: {}", status);
                    invoices = invoiceRepository.findByCompanyId(companyId);
                    total = invoiceRepository.countByCompanyId(companyId);
                }
            } else {
                invoices = invoiceRepository.findByCompanyId(companyId);
                total = invoiceRepository.countByCompanyId(companyId);
            }

            // Apply search filter if provided
            if (search != null && !search.trim().isEmpty()) {
                String searchLower = search.toLowerCase();
                invoices = invoices.stream()
                        .filter(invoice -> 
                            (invoice.getInvoiceNumber() != null && invoice.getInvoiceNumber().toLowerCase().contains(searchLower)) ||
                            (invoice.getCustomer() != null && invoice.getCustomer().getCustomerName() != null && 
                             invoice.getCustomer().getCustomerName().toLowerCase().contains(searchLower))
                        )
                        .collect(Collectors.toList());
                total = invoices.size();
            }

            // Apply pagination manually (since we're using List, not Page)
            int start = page * size;
            int end = Math.min(start + size, invoices.size());
            List<Invoice> pagedInvoices = start < invoices.size() ? invoices.subList(start, end) : List.of();

            // Build response
            Map<String, Object> response = new HashMap<>();
            response.put("invoices", pagedInvoices);
            response.put("total", total);
            response.put("page", page);
            response.put("size", size);
            response.put("totalPages", (int) Math.ceil((double) total / size));

            log.info("Successfully retrieved {} invoices for company ID: {} (showing page {})", 
                    total, companyId, page);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching invoices", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get invoice by ID for the logged-in company
     */
    @GetMapping("/invoices/{invoiceId}")
    @Operation(
            summary = "Get invoice by ID",
            description = "Retrieves a specific invoice by ID for the authenticated company."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved invoice"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Invoice not found or does not belong to company"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    public ResponseEntity<Invoice> getInvoiceById(
            HttpServletRequest request,
            @PathVariable Long invoiceId
    ) {
        log.info("Received request for invoice ID: {}", invoiceId);

        try {
            // Get company ID from JWT token
            String token = getTokenFromRequest(request);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Long companyId = jwtTokenProvider.getCompanyIdFromToken(token);
            if (companyId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Get invoice with eagerly fetched relationships
            Invoice invoice = invoiceRepository.findByIdWithRelationships(invoiceId);

            if (invoice == null) {
                log.warn("Invoice {} not found", invoiceId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Verify invoice belongs to company
            if (!invoice.getCompany().getId().equals(companyId)) {
                log.warn("Invoice {} does not belong to company {}", invoiceId, companyId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.ok(invoice);
            
        } catch (Exception e) {
            log.error("Error fetching invoice ID: {}", invoiceId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get company profile information
     * Returns the logged-in company's information
     */
    @GetMapping("/profile")
    @Operation(
            summary = "Get company profile",
            description = "Retrieves the authenticated company's profile information."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved company profile",
                    content = @Content(schema = @Schema(implementation = Company.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing token"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Company not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<Company> getCompanyProfile(HttpServletRequest request) {
        log.info("Received request for company profile");

        try {
            // Get company ID from JWT token
            String token = getTokenFromRequest(request);
            if (token == null) {
                log.warn("No token found in request");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Long companyId = jwtTokenProvider.getCompanyIdFromToken(token);
            if (companyId == null) {
                log.warn("No company ID found in token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Get company
            Company company = companyRepository.findById(companyId)
                    .orElse(null);

            if (company == null) {
                log.warn("Company not found with ID: {}", companyId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            log.info("Successfully retrieved company profile for company ID: {}", companyId);
            return ResponseEntity.ok(company);

        } catch (Exception e) {
            log.error("Error fetching company profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Mark invoice as paid (company-specific endpoint)
     * Verifies invoice belongs to company before marking as paid
     */
    @PostMapping("/invoices/{invoiceId}/mark-paid")
    @Operation(
            summary = "Mark invoice as paid",
            description = "Marks an invoice as paid for the authenticated company. " +
                         "Verifies the invoice belongs to the company before processing."
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
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing token"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Invoice does not belong to company"
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
            HttpServletRequest request,
            @PathVariable Long invoiceId,
            @Valid @RequestBody MarkInvoicePaidRequest markPaidRequest
    ) {
        log.info("Received request to mark invoice ID: {} as paid", invoiceId);

        try {
            // Get company ID from JWT token
            String token = getTokenFromRequest(request);
            if (token == null) {
                log.warn("No token found in request");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Long companyId = jwtTokenProvider.getCompanyIdFromToken(token);
            if (companyId == null) {
                log.warn("No company ID found in token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Get invoice and verify it belongs to company
            Invoice invoice = invoiceRepository.findByIdWithRelationships(invoiceId);
            if (invoice == null) {
                log.warn("Invoice {} not found", invoiceId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            if (!invoice.getCompany().getId().equals(companyId)) {
                log.warn("Invoice {} does not belong to company {}", invoiceId, companyId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Mark invoice as paid
            Invoice updatedInvoice = paymentService.markInvoiceAsPaid(invoiceId, markPaidRequest);

            log.info("Successfully marked invoice ID: {} as PAID for company ID: {}", invoiceId, companyId);
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
     * Update company profile information
     * Allows company to update their own profile details
     */
    @PutMapping("/profile")
    @Operation(
            summary = "Update company profile",
            description = "Updates the authenticated company's profile information. " +
                         "Only allows updating name, GST number, contact email, and contact phone."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully updated company profile",
                    content = @Content(schema = @Schema(implementation = Company.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing token"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Company not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<Company> updateCompanyProfile(
            HttpServletRequest request,
            @Valid @RequestBody UpdateCompanyProfileRequest updateRequest
    ) {
        log.info("Received request to update company profile");

        try {
            // Get company ID from JWT token
            String token = getTokenFromRequest(request);
            if (token == null) {
                log.warn("No token found in request");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Long companyId = jwtTokenProvider.getCompanyIdFromToken(token);
            if (companyId == null) {
                log.warn("No company ID found in token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Get company
            Company company = companyRepository.findById(companyId)
                    .orElse(null);

            if (company == null) {
                log.warn("Company not found with ID: {}", companyId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Update company fields (only allow updating specific fields)
            company.setName(updateRequest.getName());
            company.setGstNumber(updateRequest.getGstNumber());
            company.setContactEmail(updateRequest.getContactEmail());
            company.setContactPhone(updateRequest.getContactPhone());
            // Note: isActive and isApproved can only be changed by admin

            // Save updated company
            Company updatedCompany = companyRepository.save(company);

            log.info("Successfully updated company profile for company ID: {}", companyId);
            return ResponseEntity.ok(updatedCompany);

        } catch (Exception e) {
            log.error("Error updating company profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Extract JWT token from request header
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

