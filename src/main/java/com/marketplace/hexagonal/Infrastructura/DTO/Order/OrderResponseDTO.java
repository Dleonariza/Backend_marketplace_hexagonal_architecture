package com.marketplace.hexagonal.Infrastructura.DTO.Order;

import com.marketplace.hexagonal.Domain.Order.Models.Order;
import com.marketplace.hexagonal.Domain.Order.Models.SubOrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponseDTO(
        String id,
        String numOrder,
        String status,
        String idempotencyKey,

        List<String> storeNames,
        List<SubOrderResponseDTO> subOrders,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal shippingCost,
        BigDecimal total,
        String currency,
        String shippingAddressId,
        String couponCode,

        PaymentInfoResponseDTO paymentInfo,
        Instant createdAt,
        Instant updatedAt
) {
    public static OrderResponseDTO from(Order order) {
        return new OrderResponseDTO(
                order.getId().getValue(),
                order.getNumOrder(),
                order.getStatus().name(),
                order.getIdempotencyKey(),
                order.getSubOrders().stream().map(SubOrder::getStoreName).toList(),
                order.getSubOrders().stream().map(SubOrderResponseDTO::from).toList(),
                order.getSubtotal().amount(),
                order.getDiscount().amount(),
                order.getShippingCost().amount(),
                order.getTotal().amount(),
                order.getTotal().currency(),
                order.getShippingAddressId(),
                order.getCouponCode(),
                PaymentInfoResponseDTO.from(order.getPaymentInfo()),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
