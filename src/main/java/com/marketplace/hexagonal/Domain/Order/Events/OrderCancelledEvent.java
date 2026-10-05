package com.marketplace.hexagonal.Domain.Order.Events;

import com.marketplace.hexagonal.Domain.Order.Models.OrderId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.time.Instant;

public record OrderCancelledEvent(
        OrderId orderId,
        String orderNumber,
        UserId buyerUserId,
        String cancelledBy,
        Instant cancelledAt
) {}
