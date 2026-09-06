package com.officemind.api.search;

import com.officemind.application.documentchunk.SemanticSearchUseCase;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SemanticSearchUseCase semanticSearchUseCase;

    public SearchController(SemanticSearchUseCase semanticSearchUseCase) {
        this.semanticSearchUseCase = semanticSearchUseCase;
    }

    @PostMapping
    public List<SearchResultResponse> search(@Valid @RequestBody SearchRequest request) {
        return semanticSearchUseCase.execute(request.query(), request.limit()).stream()
                .map(SearchResultResponse::from)
                .toList();
    }
}
