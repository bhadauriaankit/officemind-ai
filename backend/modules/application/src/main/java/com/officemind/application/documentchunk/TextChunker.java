package com.officemind.application.documentchunk;

import java.util.ArrayList;
import java.util.List;

/**
 * Fixed-size character chunking with overlap. Simple and dependency-free;
 * good enough for a general-purpose RAG demo. Not sentence/paragraph
 * aware, so a chunk boundary can land mid-sentence -- acceptable tradeoff
 * for this project's scope.
 */
final class TextChunker {

    private static final int CHUNK_SIZE = 1000;
    private static final int OVERLAP = 200;

    private TextChunker() {
    }

    static List<String> chunk(String text) {
        List<String> chunks = new ArrayList<>();
        String trimmed = text == null ? "" : text.trim();
        if (trimmed.isEmpty()) {
            return chunks;
        }

        int start = 0;
        while (start < trimmed.length()) {
            int end = Math.min(start + CHUNK_SIZE, trimmed.length());
            String piece = trimmed.substring(start, end).trim();
            if (!piece.isEmpty()) {
                chunks.add(piece);
            }
            if (end == trimmed.length()) {
                break;
            }
            start = end - OVERLAP;
        }
        return chunks;
    }
}
