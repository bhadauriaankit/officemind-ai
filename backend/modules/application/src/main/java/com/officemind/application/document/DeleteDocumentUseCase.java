package com.officemind.application.document;

import com.officemind.common.exception.ResourceNotFoundException;
import com.officemind.domain.document.Document;
import com.officemind.domain.shared.EntityId;
import org.springframework.stereotype.Service;

@Service
public class DeleteDocumentUseCase {

    private final DocumentRepositoryPort documentRepository;
    private final FileStoragePort fileStoragePort;

    public DeleteDocumentUseCase(DocumentRepositoryPort documentRepository, FileStoragePort fileStoragePort) {
        this.documentRepository = documentRepository;
        this.fileStoragePort = fileStoragePort;
    }

    public void execute(EntityId id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", id));
        fileStoragePort.delete(document.getStorageKey());
        documentRepository.deleteById(id);
    }
}
