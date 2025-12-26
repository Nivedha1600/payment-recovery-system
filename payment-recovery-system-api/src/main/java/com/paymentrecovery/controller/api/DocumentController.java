package com.paymentrecovery.controller.api;

import com.paymentrecovery.model.dto.response.DocumentResponse;
import com.paymentrecovery.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST Controller for Document operations
 * Handles file uploads and document management
 */
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Document", description = "Document upload and management APIs")
public class DocumentController {

    private final DocumentService documentService;

    /**
     * Upload invoice document
     * Accepts PDF, Images, DOC, and Excel files
     *
     * @param file Uploaded file
     * @param companyId Company ID (required)
     * @param invoiceId Optional invoice ID
     * @param description Optional description
     * @return DocumentResponse with saved document information
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload invoice document",
            description = "Uploads an invoice document file (PDF, Image, DOC, Excel). " +
                         "Stores the file and saves metadata to database. " +
                         "Data extraction is not performed yet."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Document uploaded successfully",
                    content = @Content(schema = @Schema(implementation = DocumentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid file type or request data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Company or Invoice not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("companyId") Long companyId,
            @RequestParam(value = "invoiceId", required = false) Long invoiceId,
            @RequestParam(value = "description", required = false) String description
    ) {
        log.info(
                "Received document upload request: file={}, companyId={}, invoiceId={}",
                file.getOriginalFilename(), companyId, invoiceId
        );

        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Upload and save document
            DocumentResponse response = documentService.uploadDocument(
                    file,
                    companyId,
                    invoiceId,
                    description
            );

            log.info("Document uploaded successfully with ID: {}", response.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (jakarta.persistence.EntityNotFoundException e) {
            log.error("Entity not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error uploading document", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

