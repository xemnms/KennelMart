package com.kennel.mart.kennelmart.service.impl;

import com.kennel.mart.kennelmart.service.FileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    private static final Logger log = LoggerFactory.getLogger(FileUploadServiceImpl.class);

    @Value("${file.upload-dir:uploads/products}")
    private String uploadDir;

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Only image files are allowed");
            }

            // Create directory if not exists
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Determine file extension from content type or original filename
            String extension = getFileExtension(file);
            String filename = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(filename);

            // Save file
            Files.copy(file.getInputStream(), filePath);
            log.info("Saved image to: {} (original name: {}, size: {} bytes)", 
                filePath.toAbsolutePath(), file.getOriginalFilename(), file.getSize());

            // Return URL path (relative, will be served by backend)
            return "/uploads/products/" + filename;
        } catch (IOException e) {
            log.error("Failed to upload image", e);
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
    }

    private String getFileExtension(MultipartFile file) {
        // First, try to get extension from original filename
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            // Accept common image extensions
            if (ext.matches("\\.(jpg|jpeg|png|gif|webp|bmp|svg)")) {
                return ext.toLowerCase();
            }
        }

        // Fallback: map MIME type to extension
        String contentType = file.getContentType();
        if (contentType != null) {
            switch (contentType) {
                case "image/jpeg": return ".jpg";
                case "image/png": return ".png";
                case "image/gif": return ".gif";
                case "image/webp": return ".webp";
                case "image/bmp": return ".bmp";
                case "image/svg+xml": return ".svg";
                default: return ".jpg";
            }
        }

        return ".jpg"; // default
    }
}