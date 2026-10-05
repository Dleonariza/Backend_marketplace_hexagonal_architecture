package com.marketplace.hexagonal.Domain.Coupon.Models;

import com.marketplace.hexagonal.Domain.Coupon.Exceptions.*;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Coupon {

    private CouponId id;
    private String code;
    private String description;
    private DiscountType discountType;

    private BigDecimal discountValue;

    private BigDecimal minimumOrderAmount;

    private Integer maxUses;
    private int usedCount;
    private Instant validFrom;

    private Instant validUntil;
    private boolean active;
    private Instant createdAt;

    public static Coupon create(
            String code,
            String description,
            DiscountType discountType,
            BigDecimal discountValue,
            BigDecimal minimumOrderAmount,
            Integer maxUses,
            Instant validFrom,
            Instant validUntil) {
        if (code == null || code.isBlank())
            throw new IllegalArgumentException("Coupon code cannot be blank");
        if (discountType == DiscountType.PERCENTAGE
                && (discountValue.compareTo(BigDecimal.ZERO) <= 0
                    || discountValue.compareTo(BigDecimal.valueOf(100)) > 0))
            throw new IllegalArgumentException("Percentage discount must be between 0 and 100");
        if (discountType == DiscountType.FIXED_AMOUNT
                && discountValue.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Fixed discount amount must be positive");

        return Coupon.builder()
                .id(CouponId.generate())
                .code(code.toUpperCase().trim())
                .description(description)
                .discountType(discountType)
                .discountValue(discountValue)
                .minimumOrderAmount(minimumOrderAmount)
                .maxUses(maxUses)
                .usedCount(0)
                .validFrom(validFrom != null ? validFrom : Instant.now())
                .validUntil(validUntil)
                .active(true)
                .createdAt(Instant.now())
                .build();
    }

    public static Coupon reconstitute(
            CouponId id,
            String code,
            String description,
            DiscountType discountType,
            BigDecimal discountValue,
            BigDecimal minimumOrderAmount,
            Integer maxUses,
            int usedCount,
            Instant validFrom,
            Instant validUntil,
            boolean active,
            Instant createdAt) {
        Coupon c = new Coupon();
        c.id = id;
        c.code = code;
        c.description = description;
        c.discountType = discountType;
        c.discountValue = discountValue;
        c.minimumOrderAmount = minimumOrderAmount;
        c.maxUses = maxUses;
        c.usedCount = usedCount;
        c.validFrom = validFrom;
        c.validUntil = validUntil;
        c.active = active;
        c.createdAt = createdAt;
        return c;
    }

    public void validate(Money cartSubtotal) {
        if (!active)
            throw new CouponInactiveException(code);

        Instant now = Instant.now();
        if (now.isBefore(validFrom))
            throw new CouponInactiveException(code);
        if (validUntil != null && now.isAfter(validUntil))
            throw new CouponExpiredException(code);
        if (maxUses != null && usedCount >= maxUses)
            throw new CouponMaxUsesReachedException(code);
        if (minimumOrderAmount != null
                && cartSubtotal.amount().compareTo(minimumOrderAmount) < 0)
            throw new CouponMinimumOrderNotMetException(code, minimumOrderAmount, cartSubtotal.amount());
    }

    public Money calculateDiscount(Money subtotal) {
        BigDecimal amount;
        if (discountType == DiscountType.PERCENTAGE) {
            amount = subtotal.amount()
                    .multiply(discountValue)
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        } else {
            amount = discountValue.min(subtotal.amount());
        }
        return Money.of(amount, subtotal.currency());
    }

    public void incrementUsage() { this.usedCount++; }

    public void activate() { this.active = true; }
    public void deactivate() { this.active = false; }
}
