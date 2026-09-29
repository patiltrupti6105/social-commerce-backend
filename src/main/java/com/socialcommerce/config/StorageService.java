package com.socialcommerce.config;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

/**
 * Abstraction over file storage backends.
 * Switch between implementations via STORAGE_MODE env var.
 */
public interface StorageService {
    /**
     * Stores a file and returns its public URL.
     */
    String store(MultipartFile file) throws IOException;
}
