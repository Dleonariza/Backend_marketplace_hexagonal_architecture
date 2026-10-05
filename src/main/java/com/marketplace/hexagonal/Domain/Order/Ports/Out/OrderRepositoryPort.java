package com.marketplace.hexagonal.Domain.Order.Ports.Out;

import com.marketplace.hexagonal.Domain.Order.Models.AdminOrderSearchCriteria;
import com.marketplace.hexagonal.Domain.Order.Models.Order;
import com.marketplace.hexagonal.Domain.Order.Models.OrderId;
import com.marketplace.hexagonal.Domain.Product.Models.PageResult;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {

    Order save(Order order);

    Optional<Order> findById(OrderId id);

    Optional<Order> findByUserIdAndIdempotencyKey(UserId userId, String idempotencyKey);

    List<Order> findByUserIdOrderByCreatedAtDesc(UserId userId);

    PageResult<Order> findAllFiltered(AdminOrderSearchCriteria criteria);

    boolean existsByNumOrder(String numOrder);
}
