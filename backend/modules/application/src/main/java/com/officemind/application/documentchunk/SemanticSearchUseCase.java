package com.officemind.application.documentchunk;

import com.officemind.application.document.DocumentRepositoryPort;
import com.officemind.domain.document.Document;
import com.officemind.domain.shared.EntityId;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SemanticSearchUseCase {

    private static final int DEFAULT_LIMIT = 5;

    private final EmbeddingPort embeddingPort;
    private final VectorStorePort vectorStorePort;
    private final DocumentRepositoryPort documentRepository;

    public SemanticSearchUseCase(EmbeddingPort embeddingPort,
                                  VectorStorePort vectorStorePort,
                                  DocumentRepositoryPort documentRepository) {
        this.embeddingPort = embeddingPort;
        this.vectorStorePort = vectorStorePort;
        this.documentRepository = documentRepository;
    }

    public record Result(EntityId chunkId, EntityId documentId, String documentFileName,
                          int chunkIndex, String content, float score) {
    }

    public List<Result> execute(String query, Integer limit) {
        int effectiveLimit = (limit == null || limit <= 0) ? DEFAULT_LIMIT : limit;
        float[] queryVector = embeddingPort.embed(query);
        List<VectorStorePort.SearchHit> hits = vectorStorePort.search(queryVector, effectiveLimit);

        // Batch-resolve filenames rather than one findById per hit.
        Map<EntityId, String> fileNamesById = new HashMap<>();
        for (VectorStorePort.SearchHit hit : hits) {
            fileNamesById.computeIfAbsent(hit.documentId(), id ->
                    documentRepository.findById(id).map(Document::getFileName).orElse("(deleted document)"));
        }

        return hits.stream()
                .map(hit -> new Result(
                        hit.chunkId(),
                        hit.documentId(),
                        fileNamesById.get(hit.documentId()),
                        hit.chunkIndex(),
                        hit.content(),
                        hit.score()
                ))
                .toList();
    }
}
