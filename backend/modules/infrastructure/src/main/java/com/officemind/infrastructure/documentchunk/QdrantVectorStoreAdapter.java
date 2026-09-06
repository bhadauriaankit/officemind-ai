package com.officemind.infrastructure.documentchunk;

import com.officemind.application.documentchunk.VectorStorePort;
import com.officemind.domain.shared.EntityId;
import io.qdrant.client.ConditionFactory;
import io.qdrant.client.PointIdFactory;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.ValueFactory;
import io.qdrant.client.VectorsFactory;
import io.qdrant.client.WithPayloadSelectorFactory;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;
import io.qdrant.client.grpc.Points.Filter;
import io.qdrant.client.grpc.Points.PointStruct;
import io.qdrant.client.grpc.Points.ScoredPoint;
import io.qdrant.client.grpc.Points.SearchPoints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class QdrantVectorStoreAdapter implements VectorStorePort {

    private static final Logger log = LoggerFactory.getLogger(QdrantVectorStoreAdapter.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private static final String PAYLOAD_DOCUMENT_ID = "document_id";
    private static final String PAYLOAD_CHUNK_INDEX = "chunk_index";
    private static final String PAYLOAD_CONTENT = "content";

    private final QdrantClient qdrantClient;
    private final String collectionName;
    private final int vectorSize;

    public QdrantVectorStoreAdapter(
            QdrantClient qdrantClient,
            @Value("${officemind.qdrant.collection-name}") String collectionName,
            @Value("${officemind.qdrant.vector-size}") int vectorSize) {
        this.qdrantClient = qdrantClient;
        this.collectionName = collectionName;
        this.vectorSize = vectorSize;
    }

    @Override
    public void ensureCollectionExists() {
        try {
            boolean exists = qdrantClient.collectionExistsAsync(collectionName, TIMEOUT)
                    .get(TIMEOUT.getSeconds(), TimeUnit.SECONDS);
            if (!exists) {
                VectorParams params = VectorParams.newBuilder()
                        .setSize(vectorSize)
                        .setDistance(Distance.Cosine)
                        .build();
                qdrantClient.createCollectionAsync(collectionName, params, TIMEOUT)
                        .get(TIMEOUT.getSeconds(), TimeUnit.SECONDS);
                log.info("Created Qdrant collection: name={} size={} distance=Cosine", collectionName, vectorSize);
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to ensure Qdrant collection exists: " + collectionName, e);
        }
    }

    @Override
    public void upsert(List<IndexedChunk> chunks) {
        List<PointStruct> points = chunks.stream().map(this::toPointStruct).toList();
        try {
            qdrantClient.upsertAsync(collectionName, points, TIMEOUT)
                    .get(TIMEOUT.getSeconds(), TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to upsert chunks to Qdrant, count=" + points.size(), e);
        }
    }

    @Override
    public void deleteByDocumentId(EntityId documentId) {
        Filter filter = Filter.newBuilder()
                .addMust(ConditionFactory.matchKeyword(PAYLOAD_DOCUMENT_ID, documentId.value().toString()))
                .build();
        try {
            qdrantClient.deleteAsync(collectionName, filter, TIMEOUT)
                    .get(TIMEOUT.getSeconds(), TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to delete Qdrant points for document " + documentId.value(), e);
        }
    }

    @Override
    public List<SearchHit> search(float[] queryVector, int limit) {
        SearchPoints request = SearchPoints.newBuilder()
                .setCollectionName(collectionName)
                .addAllVector(toFloatList(queryVector))
                .setLimit(limit)
                .setWithPayload(WithPayloadSelectorFactory.enable(true))
                .build();
        try {
            List<ScoredPoint> results = qdrantClient.searchAsync(request, TIMEOUT)
                    .get(TIMEOUT.getSeconds(), TimeUnit.SECONDS);
            return results.stream().map(this::toSearchHit).toList();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to search Qdrant collection " + collectionName, e);
        }
    }

    private PointStruct toPointStruct(IndexedChunk chunk) {
        return PointStruct.newBuilder()
                .setId(PointIdFactory.id(chunk.chunkId().value()))
                .setVectors(VectorsFactory.vectors(toFloatList(chunk.vector())))
                .putAllPayload(Map.of(
                        PAYLOAD_DOCUMENT_ID, ValueFactory.value(chunk.documentId().value().toString()),
                        PAYLOAD_CHUNK_INDEX, ValueFactory.value((long) chunk.chunkIndex()),
                        PAYLOAD_CONTENT, ValueFactory.value(chunk.content())
                ))
                .build();
    }

    private SearchHit toSearchHit(ScoredPoint point) {
        Map<String, io.qdrant.client.grpc.JsonWithInt.Value> payload = point.getPayloadMap();
        UUID documentId = UUID.fromString(payload.get(PAYLOAD_DOCUMENT_ID).getStringValue());
        int chunkIndex = (int) payload.get(PAYLOAD_CHUNK_INDEX).getIntegerValue();
        String content = payload.get(PAYLOAD_CONTENT).getStringValue();
        UUID chunkId = UUID.fromString(point.getId().getUuid());
        return new SearchHit(EntityId.of(chunkId), EntityId.of(documentId), chunkIndex, content, point.getScore());
    }

    private static List<Float> toFloatList(float[] arr) {
        List<Float> list = new ArrayList<>(arr.length);
        for (float f : arr) {
            list.add(f);
        }
        return list;
    }
}
