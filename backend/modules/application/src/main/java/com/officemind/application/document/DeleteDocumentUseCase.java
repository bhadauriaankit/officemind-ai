package com.officemind.application.document;

import com.officemind.application.documentchunk.DocumentChunkRepositoryPort;
import com.officemind.application.documentchunk.VectorStorePort;
import com.officemind.common.exception.ResourceNotFoundException;
import com.officemind.domain.document.Document;
import com.officemind.domain.shared.EntityId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteDocumentUseCase {

    private final DocumentRepositoryPort documentRepository;
    private final FileStoragePort fileStoragePort;
    private final VectorStorePort vectorStorePort;
    private final DocumentChunkRepositoryPort chunkRepository;

    public DeleteDocumentUseCase(DocumentRepositoryPort documentRepository,
                                  FileStoragePort fileStoragePort,
                                  VectorStorePort vectorStorePort,
                                  DocumentChunkRepositoryPort chunkRepository) {
        this.documentRepository = documentRepository;
        this.fileStoragePort = fileStoragePort;
        this.vectorStorePort = vectorStorePort;
        this.chunkRepository = chunkRepository;
    }

    @Transactional
    public void execute(EntityId id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", id));

        // Qdrant has no FK relationship to Postgres, so this must be done
        // explicitly -- the document_chunks table's ON DELETE CASCADE (V7)
        // only cleans up Postgres metadata, not the vectors.
        vectorStorePort.deleteByDocumentId(id);
        chunkRepository.deleteByDocumentId(id);

        fileStoragePort.delete(document.getStorageKey());
        documentRepository.deleteById(id);
    }
}
