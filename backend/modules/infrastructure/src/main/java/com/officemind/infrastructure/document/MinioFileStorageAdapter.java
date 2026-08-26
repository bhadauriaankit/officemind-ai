package com.officemind.infrastructure.document;

import com.officemind.application.document.FileStoragePort;
import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class MinioFileStorageAdapter implements FileStoragePort {

    private final MinioClient minioClient;
    private final String bucket;

    public MinioFileStorageAdapter(MinioClient minioClient,
                                    @Value("${officemind.storage.default-bucket}") String bucket) {
        this.minioClient = minioClient;
        this.bucket = bucket;
    }

    @Override
    public void store(String storageKey, InputStream content, long sizeBytes, String contentType) {
        try {
            ensureBucketExists();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(storageKey)
                            .stream(content, sizeBytes, -1)
                            .contentType(contentType != null ? contentType : "application/octet-stream")
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file in MinIO: " + storageKey, e);
        }
    }

    @Override
    public InputStream retrieve(String storageKey) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder().bucket(bucket).object(storageKey).build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve file from MinIO: " + storageKey, e);
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucket).object(storageKey).build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from MinIO: " + storageKey, e);
        }
    }

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }
}
