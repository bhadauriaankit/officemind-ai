package com.officemind.infrastructure.documentchunk;

import com.officemind.application.documentchunk.DocumentChunkRepositoryPort;
import com.officemind.domain.documentchunk.DocumentChunk;
import com.officemind.domain.shared.EntityId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocumentChunkRepositoryAdapter implements DocumentChunkRepositoryPort {

    private final DocumentChunkJpaRepository jpaRepository;

    public DocumentChunkRepositoryAdapter(DocumentChunkJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<DocumentChunk> saveAll(List<DocumentChunk> chunks) {
        List<DocumentChunkJpaEntity> entities = chunks.stream()
                .map(DocumentChunkMapper::toJpa)
                .toList();
        return jpaRepository.saveAll(entities).stream()
                .map(DocumentChunkMapper::toDomain)
                .toList();
    }

    @Override
    public List<DocumentChunk> findByDocumentId(EntityId documentId) {
        return jpaRepository.findByDocumentId(documentId.value()).stream()
                .map(DocumentChunkMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteByDocumentId(EntityId documentId) {
        jpaRepository.deleteByDocumentId(documentId.value());
    }
}
