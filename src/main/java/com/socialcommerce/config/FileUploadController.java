package com.socialcommerce.config;

import com.socialcommerce.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * POST /api/v1/upload/image
 * Accepts multipart/form-data with field "file".
 * Returns { url } pointing at the stored file.
 *
 * Storage backend is controlled by the STORAGE_MODE env var:
 *   local (default) — writes to the uploads/ directory
 *   s3              — writes to AWS S3; set AWS_S3_BUCKET + AWS_S3_REGION
 */
@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final StorageService storageService;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final long MAX_SIZE = 10L * 1024 * 1024; // 10 MB

    @PostMapping("/image")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("No file provided"));
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Only JPEG, PNG, GIF, and WebP images are allowed"));
        }
        if (file.getSize() > MAX_SIZE) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("File too large (max 10 MB)"));
        }

        String url = storageService.store(file);
        return ResponseEntity.ok(ApiResponse.success(Map.of("url", url), "Upload successful"));
    }
}
