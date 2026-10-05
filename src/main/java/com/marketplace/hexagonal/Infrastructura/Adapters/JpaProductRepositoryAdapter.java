package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.Product.Models.PageResult;
import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductSearchCriteria;
import com.marketplace.hexagonal.Domain.Product.Models.ProductStatus;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Infrastructura.Entities.Product.CategoryEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Product.ProductEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Seller.SellerProfileEntity;
import com.marketplace.hexagonal.Infrastructura.Mappers.ProductMapper;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaCategoryRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaProductRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaSellerProfileRepository;
import com.marketplace.hexagonal.Infrastructura.Specifications.ProductSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class JpaProductRepositoryAdapter implements ProductRepositoryPort {

    private final JpaProductRepository jpaProductRepository;
    private final JpaSellerProfileRepository jpaSellerProfileRepository;
    private final JpaCategoryRepository jpaCategoryRepository;
    private final ProductMapper productMapper;

    @Override
    public Product save(Product product) {
        SellerProfileEntity seller = jpaSellerProfileRepository
                .findById(UUID.fromString(product.getSellerId().getValue()))
                .orElseThrow(() -> new IllegalStateException(
                        "Seller not found: " + product.getSellerId().getValue()));

        CategoryEntity category = jpaCategoryRepository
                .findById(UUID.fromString(product.getCategoryId().getValue()))
                .orElseThrow(() -> new IllegalStateException(
                        "Category not found: " + product.getCategoryId().getValue()));

        ProductEntity entity = productMapper.toEntity(product, seller, category);
        ProductEntity saved = jpaProductRepository.save(entity);
        return productMapper.toDomain(saved);
    }

    @Override
    public Optional<Product> findById(ProductId id) {
        return jpaProductRepository.findById(UUID.fromString(id.getValue()))
                .map(productMapper::toDomain);
    }

    @Override
    public List<Product> findBySellerId(SellerProfileId sellerId) {
        return jpaProductRepository.findBySeller_Id(UUID.fromString(sellerId.getValue()))
                .stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findBySellerIdAndStatus(SellerProfileId sellerId, ProductStatus status) {
        return jpaProductRepository.findBySeller_IdAndStatus(UUID.fromString(sellerId.getValue()), status)
                .stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByStatus(ProductStatus status) {
        return jpaProductRepository.findByStatus(status).stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findAllActive() {
        return jpaProductRepository.findByStatus(ProductStatus.ACTIVE).stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsBySlug(String slug) {
        return jpaProductRepository.existsBySlug(slug);
    }

    @Override
    public PageResult<Product> search(ProductSearchCriteria criteria, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProductEntity> result = jpaProductRepository.findAll(
                ProductSpecification.fromCriteria(criteria), pageable);

        List<Product> content = result.getContent().stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());

        return new PageResult<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast()
        );
    }
}
