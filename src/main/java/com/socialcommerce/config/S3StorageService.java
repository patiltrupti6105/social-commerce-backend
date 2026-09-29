package com.socialcommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

/**
 * Stores uploaded files in AWS S3.
 * Active when STORAGE_MODE=s3.
 *
 * Required env vars:
 *   AWS_S3_BUCKET   — bucket name
 *   AWS_S3_REGION   — e.g. us-east-1
 *   AWS_ACCESS_KEY_ID / AWS_SECRET_ACCESS_KEY  — or use an IAM instance role (recommended)
 *
 * The bucket must have:
 *   - Public read ACLs enabled (or use a bucket policy / CloudFront instead).
 *   - CORS rule allowing PUT from your frontend origin.
 */
@Service
@ConditionalOnProperty(name = "app.storage.mode", havingValue = "s3")
public class S3StorageService implements StorageService {

    @Value("${aws.s3.bucket-name}")
    private String bucket;

    @Value("${aws.s3.region:us-east-1}")
    private String region;

    @Override
    public String store(MultipartFile file) throws IOException {
        S3Client s3 = S3Client.builder()
                .region(Region.of(region))
                .build();

        String ext = getExtension(file.getContentType());
        String key = "uploads/" + UUID.randomUUID() + "." + ext;

        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(file.getContentType())
                        .acl(ObjectCannedACL.PUBLIC_READ)
                        .build(),
                RequestBody.fromBytes(file.getBytes())
        );

        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }

    private String getExtension(String contentType) {
        if (contentType == null) return "bin";
        return switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png"  -> "png";
            case "image/gif"  -> "gif";
            case "image/webp" -> "webp";
            default           -> "bin";
        };
    }
}
