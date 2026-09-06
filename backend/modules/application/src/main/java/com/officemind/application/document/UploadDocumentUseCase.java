package com.officemind.application.document;

import com.officemind.application.documentchunk.IndexDocumentUseCase;
import com.officemind.domain.document.Document;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
public class UploadDocumentUseCase {

    private final DocumentRepositoryPort documentRepository;
    private final FileStoragePort fileStoragePort;
    private final IndexDocumentUseCase indexDocumentUseCase;

    public UploadDocumentUseCase(DocumentRepositoryPort documentRepository,
                                  FileStoragePort fileStoragePort,
                                  IndexDocumentUseCase indexDocumentUseCase) {
        this.documentRepository = documentRepository;
        this.fileStoragePort = fileStoragePort;
        this.indexDocumentUseCase = indexDocumentUseCase;
    }

    public Document execute(String fileName, String contentType, long sizeBytes,
                             InputStream content, String uploadedByUserId) {
        String storageKey = "documents/%s/%s".formatted(UUID.randomUUID(), fileName);

        fileStoragePort.store(storageKey, content, sizeBytes, contentType);

        Document document = Document.upload(fileName, contentType, sizeBytes, storageKey, uploadedByUserId);
        document = documentRepository.save(document);

        // Synchronous for now (no async/queue infra wired up yet -- Kafka
        // sits unused, a natural upgrade path later). This means the
        // upload HTTP request blocks until indexing finishes; acceptable
        // for small documents in this project's scope, but a real
        // production system would offload this to a queue so upload
        // latency isn't coupled to embedding latency.
        indexDocumentUseCase.execute(document.getId());

        return documentRepository.findById(document.getId()).orElseThrow();
    }
}
