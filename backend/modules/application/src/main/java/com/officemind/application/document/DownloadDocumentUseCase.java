package com.officemind.application.document;

import com.officemind.common.exception.ResourceNotFoundException;
import com.officemind.domain.document.Document;
import com.officemind.domain.shared.EntityId;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class DownloadDocumentUseCase {

    private final DocumentRepositoryPort documentRepository;
    private final FileStoragePort fileStoragePort;

    public DownloadDocumentUseCase(DocumentRepositoryPort documentRepository, FileStoragePort fileStoragePort) {
        this.documentRepository = documentRepository;
        this.fileStoragePort = fileStoragePort;
    }

    public Result execute(EntityId id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", id));
        InputStream content = fileStoragePort.retrieve(document.getStorageKey());
        return new Result(document, content);
    }

    public record Result(Document document, InputStream content) {
    }
}
