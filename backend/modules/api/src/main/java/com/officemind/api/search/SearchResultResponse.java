package com.officemind.api.search;

import com.officemind.application.documentchunk.SemanticSearchUseCase;

public record SearchResultResponse(
        String chunkId,
        String documentId,
        String documentFileName,
        int chunkIndex,
        String content,
        float score
) {
    public static SearchResultResponse from(SemanticSearchUseCase.Result result) {
        return new SearchResultResponse(
                result.chunkId().value().toString(),
                result.documentId().value().toString(),
                result.documentFileName(),
                result.chunkIndex(),
                result.content(),
                result.score()
        );
    }
}
