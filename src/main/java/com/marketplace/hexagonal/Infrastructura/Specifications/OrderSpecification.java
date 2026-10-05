package com.marketplace.hexagonal.Infrastructura.Specifications;

import com.marketplace.hexagonal.Domain.Order.Models.AdminOrderSearchCriteria;
import com.marketplace.hexagonal.Domain.Order.Models.OrderStatus;
import com.marketplace.hexagonal.Infrastructura.Entities.Order.OrderEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderSpecification {

    private OrderSpecification() {}

    public static Specification<OrderEntity> fromCriteria(AdminOrderSearchCriteria c) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (c.status() != null)
                predicates.add(cb.equal(root.get("status"), c.status()));

            if (c.userId() != null && !c.userId().isBlank())
                predicates.add(cb.equal(
                        root.get("user").get("id"),
                        UUID.fromString(c.userId())));

            if (c.dateFrom() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), c.dateFrom()));

            if (c.dateTo() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), c.dateTo()));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
