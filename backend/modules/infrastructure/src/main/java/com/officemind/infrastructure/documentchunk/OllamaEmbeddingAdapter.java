package com.officemind.infrastructure.documentchunk;

import com.officemind.application.documentchunk.EmbeddingPort;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OllamaEmbeddingAdapter implements EmbeddingPort {

    private final EmbeddingModel embeddingModel;

    public OllamaEmbeddingAdapter(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @Override
    public float[] embed(String text) {
        return toFloatArray(embeddingModel.embed(text));
    }

    @Override
    public List<float[]> embedAll(List<String> texts) {
        // EmbeddingModel.embed(List<String>) is a default method on the
        // interface in spring-ai 1.0.0-M1; nothing here guarantees Ollama
        // batches these in a single HTTP call under the hood (Ollama
        // 0.3.12's /api/embeddings only accepts one prompt per request),
        // so this may just be looping per-item beneath the interface.
        return embeddingModel.embed(texts).stream()
                .map(OllamaEmbeddingAdapter::toFloatArray)
                .toList();
    }

    private static float[] toFloatArray(List<Double> doubles) {
        float[] result = new float[doubles.size()];
        for (int i = 0; i < doubles.size(); i++) {
            result[i] = doubles.get(i).floatValue();
        }
        return result;
    }
}
