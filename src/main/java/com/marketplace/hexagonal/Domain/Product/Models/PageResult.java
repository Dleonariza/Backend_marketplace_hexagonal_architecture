package com.marketplace.hexagonal.Domain.Product.Models;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public record PageResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    public <U> PageResult<U> map(Function<T, U> mapper) {
        return new PageResult<>(
                content.stream().map(mapper).collect(Collectors.toList()),
                page, size, totalElements, totalPages, last);
    }
}
