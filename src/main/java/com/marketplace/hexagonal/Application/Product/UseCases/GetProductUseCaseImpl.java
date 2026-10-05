package com.marketplace.hexagonal.Application.Product.UseCases;

import com.marketplace.hexagonal.Domain.Product.Exceptions.ProductNotFoundException;
import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Ports.In.GetProductUseCase;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;

public class GetProductUseCaseImpl extends ProductUseCaseSupport implements GetProductUseCase {

    public GetProductUseCaseImpl(ProductRepositoryPort productRepo) { super(productRepo); }

    @Override
    public Product getProductById(ProductId id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
