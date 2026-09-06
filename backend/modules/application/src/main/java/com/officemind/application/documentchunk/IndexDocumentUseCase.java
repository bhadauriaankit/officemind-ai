package com.officemind.application.documentchunk;

import com.officemind.application.document.DocumentRepositoryPort;
import com.officemind.application.document.FileStoragePort;
import com.officemind.common.exception.ResourceNotFoundException;
import com.officemind.domain.document.Document;
import com.officemind.domain.documentchunk.DocumentChunk;
import com.officemind.domain.shared.EntityId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class IndexDocumentUseCase {

    private static final Logger log = LoggerFactory.getLogger(IndexDocumentUseCase.class);

    private final DocumentRepositoryPort documentRepository;
    private final FileStoragePort fileStoragePort;
    private final TextExtractorPort textExtractorPort;
    private final EmbeddingPort embeddingPort;
    private final VectorStorePort vectorStorePort;
    private final DocumentChunkRepositoryPort chunkRepository;

    public IndexDocumentUseCase(DocumentRepositoryPort documentRepository,
                                 FileStoragePort fileStoragePort,
                                 TextExtractorPort textExtractorPort,
                                 EmbeddingPort embeddingPort,
                                 VectorStorePort vectorStorePort,
                                 DocumentChunkRepositoryPort chunkRepository) {
        this.documentRepository = documentRepository;
        this.fileStoragePort = fileStoragePort;
        this.textExtractorPort = textExtractorPort;
        this.embeddingPort = embeddingPort;
        this.vectorStorePort = vectorStorePort;
        this.chunkRepository = chunkRepository;
    }

    public void execute(EntityId documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", documentId));

        document.markProcessing();
        documentRepository.save(document);

        try {
            // Collection must exist before any operation touches it,
            // including the cleanup delete below -- on a brand-new
            // document there's nothing to delete yet, but Qdrant still
            // errors with NOT_FOUND if the collection itself is missing.
            vectorStorePort.ensureCollectionExists();

            // Clear any prior indexing for this document first (covers
            // re-index-on-retry so we don't end up with stale/duplicate
            // chunks from a previous failed or superseded attempt).
            vectorStorePort.deleteByDocumentId(documentId);
            chunkRepository.deleteByDocumentId(documentId);

            String text;
            try (InputStream content = fileStoragePort.retrieve(document.getStorageKey())) {
                text = textExtractorPort.extractText(content, document.getContentType(), document.getFileName());
            }

            List<String> pieces = TextChunker.chunk(text);
            if (pieces.isEmpty()) {
                log.warn("Document {} produced no extractable text; marking READY with zero chunks", documentId.value());
                document.markReady();
                documentRepository.save(document);
                return;
            }

            List<DocumentChunk> chunks = new ArrayList<>();
            for (int i = 0; i < pieces.size(); i++) {
                chunks.add(DocumentChunk.create(documentId, i, pieces.get(i)));
            }
            List<DocumentChunk> savedChunks = chunkRepository.saveAll(chunks);

            List<float[]> vectors = embeddingPort.embedAll(pieces);

            List<VectorStorePort.IndexedChunk> indexed = new ArrayList<>();
            for (int i = 0; i < savedChunks.size(); i++) {
                DocumentChunk c = savedChunks.get(i);
                indexed.add(new VectorStorePort.IndexedChunk(c.getId(), vectors.get(i), documentId, c.getChunkIndex(), c.getContent()));
            }
            vectorStorePort.upsert(indexed);

            document.markReady();
            documentRepository.save(document);
            log.info("Indexed document {}: {} chunks", documentId.value(), savedChunks.size());
        } catch (Exception e) {
            log.error("Failed to index document {}", documentId.value(), e);
            document.markFailed();
            documentRepository.save(document);
        }
    }
}
