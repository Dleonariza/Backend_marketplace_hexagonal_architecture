package com.marketplace.hexagonal.Domain.Order.Events;

import com.marketplace.hexagonal.Domain.Order.Models.OrderId;
import com.marketplace.hexagonal.Domain.Order.Models.SubOrderId;
import com.marketplace.hexagonal.Domain.Order.Models.SubOrderStatus;

import java.time.Instant;

public record SubOrderStatusChangedEvent(
        SubOrderId subOrderId,
        OrderId orderId,
        SubOrderStatus newStatus,
        String storeName,
        Instant updatedAt
) {}
