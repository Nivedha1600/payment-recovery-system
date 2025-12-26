package com.paymentrecovery.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Request DTO for document upload
 * Note: This is used for validation, actual file comes as MultipartFile
 */
@Data
public class UploadDocumentRequest {

    @NotNull(message = "File is required")
    private MultipartFile file;

    private Long invoiceId;

    private String description;
}

