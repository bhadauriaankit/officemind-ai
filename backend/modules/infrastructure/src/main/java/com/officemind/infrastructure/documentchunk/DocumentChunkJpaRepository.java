package com.officemind.infrastructure.documentchunk;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface DocumentChunkJpaRepository extends JpaRepository<DocumentChunkJpaEntity, UUID> {

    List<DocumentChunkJpaEntity> findByDocumentId(UUID documentId);

    void deleteByDocumentId(UUID documentId);
}
