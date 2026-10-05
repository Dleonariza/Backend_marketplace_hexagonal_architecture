package com.marketplace.hexagonal.Application.Product.UseCases;

import com.marketplace.hexagonal.Domain.Product.Exceptions.CategoryNotFoundException;
import com.marketplace.hexagonal.Domain.Product.Exceptions.SlugAlreadyExistsException;
import com.marketplace.hexagonal.Domain.Product.Models.CategoryId;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductType;
import com.marketplace.hexagonal.Domain.Product.Ports.In.CreateProductUseCase;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.CategoryRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

public class CreateProductUseCaseImpl extends ProductUseCaseSupport implements CreateProductUseCase {

    private final CategoryRepositoryPort categoryRepo;

    public CreateProductUseCaseImpl(ProductRepositoryPort productRepo,
                                    CategoryRepositoryPort categoryRepo) {
        super(productRepo);
        this.categoryRepo = categoryRepo;
    }

    @Override
    public Product createProduct(SellerProfileId sellerId, CategoryId categoryId,
                                 String name, String description, String slug,
                                 Money basePrice, ProductType productType) {
        if (!categoryRepo.findById(categoryId).isPresent())
            throw new CategoryNotFoundException(categoryId);

        if (productRepo.existsBySlug(slug))
            throw new SlugAlreadyExistsException(slug);

        return productRepo.save(Product.create(sellerId, categoryId, name, description, slug, basePrice, productType));
    }
}
