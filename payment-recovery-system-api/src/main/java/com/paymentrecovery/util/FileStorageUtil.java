package com.paymentrecovery.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Utility class for file storage operations
 */
@Component
@Slf4j
public class FileStorageUtil {

    @Value("${app.file.upload-dir:uploads}")
    private String uploadDir;

    /**
     * Store uploaded file and return the file path
     *
     * @param file Multipart file
     * @param companyId Company ID for organizing files
     * @return Stored file path
     * @throws IOException if file storage fails
     */
    public String storeFile(MultipartFile file, Long companyId) throws IOException {
        // Generate unique file name
        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);
        String storedFileName = generateUniqueFileName(fileExtension);
        
        // Create directory structure: uploads/companyId/YYYY/MM/DD/
        LocalDate today = LocalDate.now();
        String datePath = today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path companyDir = Paths.get(uploadDir, String.valueOf(companyId), datePath);
        
        // Create directories if they don't exist
        Files.createDirectories(companyDir);
        
        // Store file
        Path targetLocation = companyDir.resolve(storedFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        // Return relative path from upload directory
        String relativePath = Paths.get(String.valueOf(companyId), datePath, storedFileName)
                .toString()
                .replace("\\", "/");
        
        log.info("File stored successfully: {}", relativePath);
        return relativePath;
    }

    /**
     * Get full file path
     *
     * @param relativePath Relative file path
     * @return Full file path
     */
    public String getFullPath(String relativePath) {
        return Paths.get(uploadDir, relativePath).toString();
    }

    /**
     * Delete file
     *
     * @param relativePath Relative file path
     * @return True if deleted successfully
     */
    public boolean deleteFile(String relativePath) {
        try {
            Path filePath = Paths.get(uploadDir, relativePath);
            Files.deleteIfExists(filePath);
            log.info("File deleted: {}", relativePath);
            return true;
        } catch (IOException e) {
            log.error("Error deleting file: {}", relativePath, e);
            return false;
        }
    }

    /**
     * Generate unique file name
     *
     * @param extension File extension
     * @return Unique file name
     */
    private String generateUniqueFileName(String extension) {
        return UUID.randomUUID().toString() + "." + extension;
    }

    /**
     * Get file extension from file name
     *
     * @param fileName File name
     * @return File extension (without dot)
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "bin";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }
}

