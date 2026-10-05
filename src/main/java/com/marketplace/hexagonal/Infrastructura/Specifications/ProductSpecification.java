package com.marketplace.hexagonal.Infrastructura.Specifications;

import com.marketplace.hexagonal.Domain.Product.Models.ProductSearchCriteria;
import com.marketplace.hexagonal.Domain.Product.Models.ProductStatus;
import com.marketplace.hexagonal.Infrastructura.Entities.Product.ProductEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductSpecification {

    private ProductSpecification() {}

    public static Specification<ProductEntity> fromCriteria(ProductSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("status"), ProductStatus.ACTIVE));

            if (criteria.keyword() != null && !criteria.keyword().isBlank()) {
                String pattern = "%" + criteria.keyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                ));
            }

            if (criteria.categoryId() != null) {
                predicates.add(cb.equal(
                        root.get("category").get("id"),
                        UUID.fromString(criteria.categoryId().getValue())
                ));
            }

            if (criteria.sellerId() != null) {
                predicates.add(cb.equal(
                        root.get("seller").get("id"),
                        UUID.fromString(criteria.sellerId().getValue())
                ));
            }

            if (criteria.minPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("basePriceAmount"), criteria.minPrice()
                ));
            }

            if (criteria.maxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("basePriceAmount"), criteria.maxPrice()
                ));
            }

            if (criteria.productType() != null) {
                predicates.add(cb.equal(root.get("productType"), criteria.productType()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
