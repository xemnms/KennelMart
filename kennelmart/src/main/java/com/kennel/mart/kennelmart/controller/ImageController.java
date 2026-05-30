package com.kennel.mart.kennelmart.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/uploads")
public class ImageController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ImageController.class);

    @GetMapping("/{subdir}/{filename:.+}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Resource> getImage(@PathVariable String subdir, @PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads", subdir, filename).normalize();
            log.info("Attempting to serve: {}", filePath.toAbsolutePath());
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                String contentType = determineContentType(filename);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .body(resource);
            } else {
                log.warn("File not found or not readable: {}", filePath.toAbsolutePath());
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error serving image: {}", e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    private String determineContentType(String filename) {
        String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        switch (ext) {
            case "jpg": case "jpeg": return MediaType.IMAGE_JPEG_VALUE;
            case "png": return MediaType.IMAGE_PNG_VALUE;
            case "gif": return MediaType.IMAGE_GIF_VALUE;
            case "webp": return "image/webp";
            case "bmp": return "image/bmp";
            case "svg": return "image/svg+xml";
            default: return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
    }
}