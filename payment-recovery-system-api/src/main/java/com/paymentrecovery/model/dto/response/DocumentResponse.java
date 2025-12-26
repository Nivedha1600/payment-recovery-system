package com.paymentrecovery.model.dto.response;

import com.paymentrecovery.model.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for document
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {

    private Long id;
    private Long companyId;
    private Long invoiceId;
    private String originalFileName;
    private String storedFileName;
    private String filePath;
    private DocumentType documentType;
    private String mimeType;
    private Long fileSize;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

