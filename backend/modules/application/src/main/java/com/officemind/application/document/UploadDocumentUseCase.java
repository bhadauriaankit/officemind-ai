package com.officemind.application.document;

import com.officemind.domain.document.Document;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
public class UploadDocumentUseCase {

    private final DocumentRepositoryPort documentRepository;
    private final FileStoragePort fileStoragePort;

    public UploadDocumentUseCase(DocumentRepositoryPort documentRepository, FileStoragePort fileStoragePort) {
        this.documentRepository = documentRepository;
        this.fileStoragePort = fileStoragePort;
    }

    public Document execute(String fileName, String contentType, long sizeBytes,
                             InputStream content, String uploadedByUserId) {
        String storageKey = "documents/%s/%s".formatted(UUID.randomUUID(), fileName);

        fileStoragePort.store(storageKey, content, sizeBytes, contentType);

        Document document = Document.upload(fileName, contentType, sizeBytes, storageKey, uploadedByUserId);
        return documentRepository.save(document);
    }
}
