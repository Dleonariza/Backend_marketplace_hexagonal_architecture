package com.marketplace.hexagonal.Infrastructura.DTO.Order;

import com.marketplace.hexagonal.Domain.Order.Models.SubOrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record SubOrderResponseDTO(
        String id,
        String orderId,
        String sellerId,
        String storeName,
        List<OrderItemResponseDTO> items,
        BigDecimal subtotal,
        BigDecimal shippingCost,
        BigDecimal total,
        String currency,
        String shippingOptionId,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
    public static SubOrderResponseDTO from(SubOrder subOrder) {
        return new SubOrderResponseDTO(
                subOrder.getId().getValue(),
                subOrder.getOrderId().getValue(),
                subOrder.getSellerId().getValue(),
                subOrder.getStoreName(),
                subOrder.getItems().stream().map(OrderItemResponseDTO::from).toList(),
                subOrder.getSubtotal().amount(),
                subOrder.getShippingCost().amount(),
                subOrder.getTotal().amount(),
                subOrder.getTotal().currency(),
                subOrder.getShippingOptionId(),
                subOrder.getStatus().name(),
                subOrder.getCreatedAt(),
                subOrder.getUpdatedAt()
        );
    }
}
