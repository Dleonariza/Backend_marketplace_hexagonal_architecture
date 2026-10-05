package com.marketplace.hexagonal.Domain.Cart.Models;

import com.marketplace.hexagonal.Domain.Cart.Exceptions.CartAlreadyCheckedOutException;
import com.marketplace.hexagonal.Domain.Cart.Exceptions.CartItemNotFoundException;
import com.marketplace.hexagonal.Domain.Coupon.Models.AppliedCoupon;
import com.marketplace.hexagonal.Domain.Coupon.Models.Coupon;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductVariantId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Cart {

    public static final int TTL_DAYS = 7;

    private CartId id;
    private UserId userId;
    private String sessionId;
    private List<CartItem> items;
    private CartStatus status;
    private AppliedCoupon appliedCoupon;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant expiresAt;

    public static Cart create(UserId userId) {
        Instant now = Instant.now();
        return Cart.builder()
                .id(CartId.generate())
                .userId(userId)
                .sessionId(null)
                .items(new ArrayList<>())
                .status(CartStatus.ACTIVE)
                .appliedCoupon(null)
                .createdAt(now)
                .updatedAt(now)
                .expiresAt(now.plus(TTL_DAYS, ChronoUnit.DAYS))
                .build();
    }

    public static Cart createAnonymous(String sessionId) {
        Instant now = Instant.now();
        return Cart.builder()
                .id(CartId.generate())
                .userId(null)
                .sessionId(sessionId)
                .items(new ArrayList<>())
                .status(CartStatus.ACTIVE)
                .appliedCoupon(null)
                .createdAt(now)
                .updatedAt(now)
                .expiresAt(now.plus(TTL_DAYS, ChronoUnit.DAYS))
                .build();
    }

    public static Cart reconstitute(
            CartId id,
            UserId userId,
            String sessionId,
            List<CartItem> items,
            CartStatus status,
            AppliedCoupon appliedCoupon,
            Instant createdAt,
            Instant updatedAt,
            Instant expiresAt) {
        Cart cart = new Cart();
        cart.id = id;
        cart.userId = userId;
        cart.sessionId = sessionId;
        cart.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        cart.status = status;
        cart.appliedCoupon = appliedCoupon;
        cart.createdAt = createdAt;
        cart.updatedAt = updatedAt;
        cart.expiresAt = expiresAt;
        return cart;
    }

    public boolean isAnonymous() { return userId == null && sessionId != null; }

    public CartItem addItem(
            ProductId productId,
            ProductVariantId variantId,
            String variantSku,
            Map<String, String> variantAttributes,
            SellerProfileId sellerId,
            int quantity,
            Money unitPrice,
            String productName,
            String productSlug,
            String primaryImageUrl) {
        requireActive();

        Optional<CartItem> existing = findItemByProduct(productId, variantId);
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.updateQuantity(item.getQuantity() + quantity);
            refreshExpiry();
            return item;
        }

        CartItem item = CartItem.create(
                productId, variantId, variantSku, variantAttributes, sellerId,
                quantity, unitPrice, productName, productSlug, primaryImageUrl);
        this.items.add(item);
        refreshExpiry();
        return item;
    }

    public void removeItem(CartItemId itemId) {
        requireActive();
        CartItem item = findItem(itemId)
                .orElseThrow(() -> new CartItemNotFoundException(itemId.getValue()));
        this.items.remove(item);
        refreshExpiry();
    }

    public CartItem updateItemQuantity(CartItemId itemId, int newQuantity) {
        requireActive();
        CartItem item = findItem(itemId)
                .orElseThrow(() -> new CartItemNotFoundException(itemId.getValue()));
        item.updateQuantity(newQuantity);
        refreshExpiry();
        return item;
    }

    public void clear() {
        requireActive();
        this.items.clear();
        refreshExpiry();
    }

    public void checkout() {
        requireActive();
        this.status = CartStatus.CHECKED_OUT;
        this.updatedAt = Instant.now();
    }

    public void abandon() {
        if (this.status == CartStatus.ACTIVE) {
            this.status = CartStatus.ABANDONED;
            this.updatedAt = Instant.now();
        }
    }

    public Optional<CartItem> findItem(CartItemId itemId) {
        return items.stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst();
    }

    public Optional<CartItem> findItemByProduct(ProductId productId, ProductVariantId variantId) {
        return items.stream()
                .filter(i -> i.matchesProduct(productId, variantId))
                .findFirst();
    }

    public Money getTotal() {
        if (items.isEmpty()) return Money.of(BigDecimal.ZERO, "USD");
        return items.stream()
                .map(i -> Money.of(
                        i.getUnitPrice().amount().multiply(BigDecimal.valueOf(i.getQuantity())),
                        i.getUnitPrice().currency()))
                .reduce((a, b) -> Money.of(a.amount().add(b.amount()), a.currency()))
                .orElse(Money.of(BigDecimal.ZERO, "USD"));
    }

    public void applyCoupon(Coupon coupon) {
        requireActive();
        Money subtotal = getTotal();
        coupon.validate(subtotal);
        Money discountAmount = coupon.calculateDiscount(subtotal);
        this.appliedCoupon = new AppliedCoupon(
                coupon.getCode(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                discountAmount);
        refreshExpiry();
    }

    public void removeCoupon() {
        requireActive();
        this.appliedCoupon = null;
        refreshExpiry();
    }

    public Money getDiscount() {
        if (appliedCoupon == null)
            return Money.of(BigDecimal.ZERO, getTotal().currency());
        return appliedCoupon.discountAmount();
    }

    public Money getFinalTotal() {
        Money total = getTotal();
        Money discount = getDiscount();
        BigDecimal net = total.amount().subtract(discount.amount()).max(BigDecimal.ZERO);
        return Money.of(net, total.currency());
    }

    public boolean isEmpty() { return items.isEmpty(); }

    public boolean isExpired() { return Instant.now().isAfter(this.expiresAt); }

    private void refreshExpiry() {
        Instant now = Instant.now();
        this.updatedAt = now;
        this.expiresAt = now.plus(TTL_DAYS, ChronoUnit.DAYS);
    }

    private void requireActive() {
        if (this.status != CartStatus.ACTIVE)
            throw new CartAlreadyCheckedOutException(
                    "Cannot modify cart with status: " + this.status);
    }
}
