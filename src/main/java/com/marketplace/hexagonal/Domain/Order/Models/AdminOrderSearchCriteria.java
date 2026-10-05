package com.marketplace.hexagonal.Domain.Order.Models;

import java.time.Instant;

public record AdminOrderSearchCriteria(
        OrderStatus status,
        String userId,
        Instant dateFrom,
        Instant dateTo,
        int page,
        int size
) {}
