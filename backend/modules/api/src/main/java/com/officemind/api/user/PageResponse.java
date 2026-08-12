package com.officemind.api.user;

import com.officemind.common.paging.PageResult;

import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(List<T> items, int page, int size, long totalElements, int totalPages) {

    public static <S, T> PageResponse<T> from(PageResult<S> source, Function<S, T> mapper) {
        return new PageResponse<>(
                source.items().stream().map(mapper).toList(),
                source.page(),
                source.size(),
                source.totalElements(),
                source.totalPages()
        );
    }
}
