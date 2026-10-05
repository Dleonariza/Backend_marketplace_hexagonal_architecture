package com.marketplace.hexagonal.Infrastructura.DTO.Product;

import com.marketplace.hexagonal.Domain.Product.Models.PageResult;
import com.marketplace.hexagonal.Domain.Product.Models.Product;

import java.util.List;
import java.util.stream.Collectors;

public record PagedProductResponseDTO(
        List<ProductResponseDTO> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    public static PagedProductResponseDTO from(PageResult<Product> pageResult) {
        List<ProductResponseDTO> content = pageResult.content().stream()
                .map(ProductResponseDTO::from)
                .collect(Collectors.toList());

        return new PagedProductResponseDTO(
                content,
                pageResult.page(),
                pageResult.size(),
                pageResult.totalElements(),
                pageResult.totalPages(),
                pageResult.last()
        );
    }
}
