package com.officemind.api.search;

import jakarta.validation.constraints.NotBlank;

public record SearchRequest(
        @NotBlank(message = "query is required") String query,
        Integer limit
) {
}
