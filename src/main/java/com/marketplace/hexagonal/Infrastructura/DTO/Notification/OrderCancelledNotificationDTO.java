package com.marketplace.hexagonal.Infrastructura.DTO.Notification;

import com.marketplace.hexagonal.Domain.Order.Events.OrderCancelledEvent;

import java.time.Instant;

public record OrderCancelledNotificationDTO(
        String type,
        String orderId,
        String orderNumber,
        String cancelledBy,
        Instant cancelledAt
) {
    public static OrderCancelledNotificationDTO from(OrderCancelledEvent event) {
        return new OrderCancelledNotificationDTO(
                "ORDER_CANCELLED",
                event.orderId().getValue(),
                event.orderNumber(),
                event.cancelledBy(),
                event.cancelledAt()
        );
    }
}
