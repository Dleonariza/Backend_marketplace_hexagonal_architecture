package com.marketplace.hexagonal.Application.Product.UseCases;

import com.marketplace.hexagonal.Domain.Product.Exceptions.CategoryNotFoundException;
import com.marketplace.hexagonal.Domain.Product.Exceptions.SlugAlreadyExistsException;
import com.marketplace.hexagonal.Domain.Product.Models.CategoryId;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductType;
import com.marketplace.hexagonal.Domain.Product.Ports.In.UpdateProductUseCase;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.CategoryRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

public class UpdateProductUseCaseImpl extends ProductUseCaseSupport implements UpdateProductUseCase {

    private final CategoryRepositoryPort categoryRepo;

    public UpdateProductUseCaseImpl(ProductRepositoryPort productRepo,
                                    CategoryRepositoryPort categoryRepo) {
        super(productRepo);
        this.categoryRepo = categoryRepo;
    }

    @Override
    public Product updateProduct(ProductId id, SellerProfileId sellerId, CategoryId categoryId,
                                 String name, String description, String slug,
                                 Money basePrice, ProductType productType) {
        Product product = loadAndVerifyOwnership(id, sellerId);

        if (!categoryRepo.findById(categoryId).isPresent())
            throw new CategoryNotFoundException(categoryId);

        if (!product.getSlug().equals(slug) && productRepo.existsBySlug(slug))
            throw new SlugAlreadyExistsException(slug);

        product.updateInfo(categoryId, name, description, slug, basePrice, productType);
        return productRepo.save(product);
    }
}
