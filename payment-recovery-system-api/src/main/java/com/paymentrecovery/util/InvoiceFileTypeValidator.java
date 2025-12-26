package com.paymentrecovery.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class to validate invoice file types
 */
@Component
public class InvoiceFileTypeValidator {

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "pdf", "png", "jpg", "jpeg", "doc", "docx", "xls", "xlsx"
    );

    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "application/pdf",
            "image/png",
            "image/jpeg",
            "image/jpg",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );

    /**
     * Validate if file type is allowed
     *
     * @param file Multipart file
     * @return True if file type is allowed
     */
    public boolean isValidFileType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();

        // Check by file extension
        if (fileName != null) {
            String extension = getFileExtension(fileName).toLowerCase();
            if (ALLOWED_EXTENSIONS.contains(extension)) {
                return true;
            }
        }

        // Check by MIME type
        if (contentType != null && ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            return true;
        }

        return false;
    }

    /**
     * Get file extension from file name
     *
     * @param fileName File name
     * @return File extension (without dot)
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    /**
     * Get allowed file types as string for error messages
     *
     * @return Comma-separated list of allowed extensions
     */
    public String getAllowedFileTypes() {
        return String.join(", ", ALLOWED_EXTENSIONS);
    }
}

