package com.marketplace.hexagonal.Domain.Order.Models;

import com.marketplace.hexagonal.Domain.Cart.Models.CartItem;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class SubOrder {

    private SubOrderId id;

    private OrderId orderId;
    private SellerProfileId sellerId;

    private String storeName;

    private List<OrderItem> items;

    private Money subtotal;

    private Money shippingCost;

    private Money total;

    private String shippingOptionId;

    private SubOrderStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public static SubOrder create(
            OrderId orderId,
            SellerProfileId sellerId,
            String storeName,
            List<CartItem> sellerItems,
            Money shippingCost,
            String shippingOptionId) {
        List<OrderItem> items = sellerItems.stream()
                .map(OrderItem::fromCartItem)
                .toList();

        String currency = items.getFirst().getUnitPrice().currency();

        Money subtotal = items.stream()
                .map(OrderItem::getItemSubtotal)
                .reduce((a, b) -> Money.of(a.amount().add(b.amount()), a.currency()))
                .orElse(Money.of(BigDecimal.ZERO, currency));

        Money total = Money.of(
                subtotal.amount().add(shippingCost.amount()), currency);

        Instant now = Instant.now();
        return SubOrder.builder()
                .id(SubOrderId.generate())
                .orderId(orderId)
                .sellerId(sellerId)
                .storeName(storeName)
                .items(items)
                .subtotal(subtotal)
                .shippingCost(shippingCost)
                .total(total)
                .shippingOptionId(shippingOptionId)
                .status(SubOrderStatus.PENDING_PAYMENT)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public static SubOrder reconstitute(
            SubOrderId id,
            OrderId orderId,
            SellerProfileId sellerId,
            String storeName,
            List<OrderItem> items,
            Money subtotal,
            Money shippingCost,
            Money total,
            String shippingOptionId,
            SubOrderStatus status,
            Instant createdAt,
            Instant updatedAt) {
        SubOrder s = new SubOrder();
        s.id = id;
        s.orderId = orderId;
        s.sellerId = sellerId;
        s.storeName = storeName;
        s.items = items;
        s.subtotal = subtotal;
        s.shippingCost = shippingCost;
        s.total = total;
        s.shippingOptionId = shippingOptionId;
        s.status = status;
        s.createdAt = createdAt;
        s.updatedAt = updatedAt;
        return s;
    }

    public void markPaid() {
        requireStatus(SubOrderStatus.PENDING_PAYMENT);
        this.status = SubOrderStatus.PAID;
        this.updatedAt = Instant.now();
    }

    public void markProcessing() {
        requireStatus(SubOrderStatus.PAID);
        this.status = SubOrderStatus.PROCESSING;
        this.updatedAt = Instant.now();
    }

    public void markShipped() {
        requireStatus(SubOrderStatus.PROCESSING);
        this.status = SubOrderStatus.SHIPPED;
        this.updatedAt = Instant.now();
    }

    public void markDelivered() {
        requireStatus(SubOrderStatus.SHIPPED);
        this.status = SubOrderStatus.DELIVERED;
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        if (status == SubOrderStatus.SHIPPED || status == SubOrderStatus.DELIVERED)
            throw new IllegalStateException("Cannot cancel a shipped or delivered sub-order.");
        this.status = SubOrderStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    public void forceCancel() {
        if (status == SubOrderStatus.DELIVERED
                || status == SubOrderStatus.CANCELLED
                || status == SubOrderStatus.REFUNDED)
            return;
        this.status = SubOrderStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    public void markRefunded() {
        requireStatus(SubOrderStatus.CANCELLED);
        this.status = SubOrderStatus.REFUNDED;
        this.updatedAt = Instant.now();
    }

    private void requireStatus(SubOrderStatus expected) {
        if (this.status != expected)
            throw new IllegalStateException(
                    "Expected sub-order status " + expected + " but was " + this.status);
    }
}
