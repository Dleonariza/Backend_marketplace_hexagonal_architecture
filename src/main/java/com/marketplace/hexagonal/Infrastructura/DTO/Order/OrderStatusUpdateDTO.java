package com.marketplace.hexagonal.Infrastructura.DTO.Order;

import com.marketplace.hexagonal.Domain.Order.Events.SubOrderStatusChangedEvent;

import java.time.Instant;

public record OrderStatusUpdateDTO(
        String orderId,
        String subOrderId,
        String storeName,
        String status,
        Instant updatedAt
) {
    public static OrderStatusUpdateDTO from(SubOrderStatusChangedEvent event) {
        return new OrderStatusUpdateDTO(
                event.orderId().getValue(),
                event.subOrderId().getValue(),
                event.storeName(),
                event.newStatus().name(),
                event.updatedAt()
        );
    }
}
