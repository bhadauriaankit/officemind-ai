package com.officemind.application.documentchunk;

import com.officemind.domain.documentchunk.DocumentChunk;
import com.officemind.domain.shared.EntityId;

import java.util.List;

public interface DocumentChunkRepositoryPort {

    List<DocumentChunk> saveAll(List<DocumentChunk> chunks);

    List<DocumentChunk> findByDocumentId(EntityId documentId);

    void deleteByDocumentId(EntityId documentId);
}
