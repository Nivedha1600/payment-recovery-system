package com.paymentrecovery.util;

import com.paymentrecovery.model.enums.DocumentType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Utility class to detect document type from file
 */
@Component
public class DocumentTypeDetector {

    /**
     * Detect document type from file
     *
     * @param file Multipart file
     * @return DocumentType enum
     */
    public DocumentType detectDocumentType(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        
        if (contentType == null && fileName != null) {
            return detectFromFileName(fileName);
        }
        
        if (contentType == null) {
            return DocumentType.OTHER;
        }
        
        // Check by MIME type
        if (contentType.equals("application/pdf")) {
            return DocumentType.PDF;
        }
        
        if (contentType.startsWith("image/")) {
            return DocumentType.IMAGE;
        }
        
        if (contentType.equals("application/msword") || 
            contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            return DocumentType.DOC;
        }
        
        if (contentType.equals("application/vnd.ms-excel") ||
            contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return DocumentType.EXCEL;
        }
        
        // Fallback to file name extension
        if (fileName != null) {
            return detectFromFileName(fileName);
        }
        
        return DocumentType.OTHER;
    }

    /**
     * Detect document type from file name extension
     *
     * @param fileName File name
     * @return DocumentType enum
     */
    private DocumentType detectFromFileName(String fileName) {
        String lowerFileName = fileName.toLowerCase();
        
        if (lowerFileName.endsWith(".pdf")) {
            return DocumentType.PDF;
        }
        
        if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg") ||
            lowerFileName.endsWith(".png") || lowerFileName.endsWith(".gif") ||
            lowerFileName.endsWith(".bmp") || lowerFileName.endsWith(".webp")) {
            return DocumentType.IMAGE;
        }
        
        if (lowerFileName.endsWith(".doc") || lowerFileName.endsWith(".docx")) {
            return DocumentType.DOC;
        }
        
        if (lowerFileName.endsWith(".xls") || lowerFileName.endsWith(".xlsx")) {
            return DocumentType.EXCEL;
        }
        
        return DocumentType.OTHER;
    }

    /**
     * Check if file type is allowed
     *
     * @param file Multipart file
     * @return True if file type is allowed
     */
    public boolean isAllowedFileType(MultipartFile file) {
        DocumentType type = detectDocumentType(file);
        return type != DocumentType.OTHER;
    }
}

