package com.marketplace.hexagonal.Application.Product.Services;

import com.marketplace.hexagonal.Domain.Product.Events.LowStockAlertEvent;
import com.marketplace.hexagonal.Domain.Product.Models.*;
import com.marketplace.hexagonal.Domain.Product.Ports.In.*;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class ProductService implements
        CreateProductUseCase,
        UpdateProductUseCase,
        PublishProductUseCase,
        DeactivateProductUseCase,
        ReactivateProductUseCase,
        DeleteProductUseCase,
        GetProductUseCase,
        ListProductsUseCase,
        SearchProductsUseCase,
        AddProductVariantUseCase,
        UpdateProductVariantUseCase,
        RemoveProductVariantUseCase,
        AddProductImageUseCase,
        RemoveProductImageUseCase,
        SetPrimaryImageUseCase {
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final PublishProductUseCase publishProductUseCase;
    private final DeactivateProductUseCase deactivateProductUseCase;
    private final ReactivateProductUseCase reactivateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final ListProductsUseCase listProductsUseCase;
    private final SearchProductsUseCase searchProductsUseCase;
    private final AddProductVariantUseCase addProductVariantUseCase;
    private final UpdateProductVariantUseCase updateProductVariantUseCase;
    private final RemoveProductVariantUseCase removeProductVariantUseCase;
    private final AddProductImageUseCase addProductImageUseCase;
    private final RemoveProductImageUseCase removeProductImageUseCase;
    private final SetPrimaryImageUseCase setPrimaryImageUseCase;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Product createProduct(SellerProfileId sellerId, CategoryId categoryId,
                                 String name, String description, String slug,
                                 Money basePrice, ProductType productType) {
        return createProductUseCase.createProduct(sellerId, categoryId, name, description, slug, basePrice, productType);
    }

    @Override
    public Product updateProduct(ProductId id, SellerProfileId sellerId, CategoryId categoryId,
                                 String name, String description, String slug,
                                 Money basePrice, ProductType productType) {
        return updateProductUseCase.updateProduct(id, sellerId, categoryId, name, description, slug, basePrice, productType);
    }

    @Override
    public Product publishProduct(ProductId id, SellerProfileId sellerId) {
        return publishProductUseCase.publishProduct(id, sellerId);
    }

    @Override
    public Product deactivateProduct(ProductId id, SellerProfileId sellerId) {
        return deactivateProductUseCase.deactivateProduct(id, sellerId);
    }

    @Override
    public Product reactivateProduct(ProductId id, SellerProfileId sellerId) {
        return reactivateProductUseCase.reactivateProduct(id, sellerId);
    }

    @Override
    public void deleteProduct(ProductId id, SellerProfileId sellerId) {
        deleteProductUseCase.deleteProduct(id, sellerId);
    }

    @Override
    public Product getProductById(ProductId id) {
        return getProductUseCase.getProductById(id);
    }

    @Override
    public List<Product> getAllActiveProducts() {
        return listProductsUseCase.getAllActiveProducts();
    }

    @Override
    public List<Product> getProductsBySeller(SellerProfileId sellerId) {
        return listProductsUseCase.getProductsBySeller(sellerId);
    }

    @Override
    public List<Product> getProductsBySellerAndStatus(SellerProfileId sellerId, ProductStatus status) {
        return listProductsUseCase.getProductsBySellerAndStatus(sellerId, status);
    }

    @Override
    public PageResult<Product> searchProducts(ProductSearchCriteria criteria, int page, int size) {
        return searchProductsUseCase.searchProducts(criteria, page, size);
    }

    @Override
    public Product addVariant(ProductId id, SellerProfileId sellerId,
                              String sku, Map<String, String> attributes, Money price, int stock) {
        return addProductVariantUseCase.addVariant(id, sellerId, sku, attributes, price, stock);
    }

    @Override
    public Product updateVariant(ProductId id, SellerProfileId sellerId, ProductVariantId variantId,
                                 String sku, Map<String, String> attributes, Money price, int stock) {
        Product saved = updateProductVariantUseCase.updateVariant(
                id, sellerId, variantId, sku, attributes, price, stock);

        if (stock <= LowStockAlertEvent.LOW_STOCK_THRESHOLD) {
            eventPublisher.publishEvent(new LowStockAlertEvent(
                    id, saved.getName(), variantId, sku, stock, sellerId));
        }

        return saved;
    }

    @Override
    public Product removeVariant(ProductId id, SellerProfileId sellerId, ProductVariantId variantId) {
        return removeProductVariantUseCase.removeVariant(id, sellerId, variantId);
    }

    @Override
    public Product addImage(ProductId id, SellerProfileId sellerId, String url, String altText) {
        return addProductImageUseCase.addImage(id, sellerId, url, altText);
    }

    @Override
    public Product removeImage(ProductId id, SellerProfileId sellerId, ProductImageId imageId) {
        return removeProductImageUseCase.removeImage(id, sellerId, imageId);
    }

    @Override
    public Product setPrimaryImage(ProductId id, SellerProfileId sellerId, ProductImageId imageId) {
        return setPrimaryImageUseCase.setPrimaryImage(id, sellerId, imageId);
    }
}
