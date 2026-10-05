package com.marketplace.hexagonal.Infrastructura.DTO.Order;

import com.marketplace.hexagonal.Domain.Order.Models.Order;
import com.marketplace.hexagonal.Domain.Order.Models.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record AdminOrderSummaryDTO(
        String orderId,
        String numOrder,
        String userId,
        OrderStatus status,
        BigDecimal total,
        String currency,
        int subOrderCount,
        String couponCode,
        Instant createdAt
) {
    public static AdminOrderSummaryDTO from(Order order) {
        return new AdminOrderSummaryDTO(
                order.getId().getValue(),
                order.getNumOrder(),
                order.getUserId().getValue(),
                order.getStatus(),
                order.getTotal().amount(),
                order.getTotal().currency(),
                order.getSubOrders().size(),
                order.getCouponCode(),
                order.getCreatedAt()
        );
    }
}
