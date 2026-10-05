package com.marketplace.hexagonal.Application.Product.UseCases;

import com.marketplace.hexagonal.Domain.Product.Models.PageResult;
import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductSearchCriteria;
import com.marketplace.hexagonal.Domain.Product.Ports.In.SearchProductsUseCase;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SearchProductsUseCaseImpl implements SearchProductsUseCase {

    private final ProductRepositoryPort productRepo;

    @Override
    public PageResult<Product> searchProducts(ProductSearchCriteria criteria, int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), 100);
        return productRepo.search(criteria, safePage, safeSize);
    }
}
