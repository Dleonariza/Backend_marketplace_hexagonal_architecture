package com.marketplace.hexagonal.Domain.Coupon;

import com.marketplace.hexagonal.Domain.Coupon.Exceptions.*;
import com.marketplace.hexagonal.Domain.Coupon.Models.Coupon;
import com.marketplace.hexagonal.Domain.Coupon.Models.DiscountType;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.*;

class CouponTest {

    private static final Money SUBTOTAL_100 = Money.of(new BigDecimal("100.00"), "USD");
    private static final Money SUBTOTAL_30 = Money.of(new BigDecimal("30.00"), "USD");

    @Test
    void create_shouldGenerateIdAndNormalizeCode() {
        Coupon coupon = buildPercentage("save10", 10, null, null, null, null);

        assertThat(coupon.getId()).isNotNull();
        assertThat(coupon.getCode()).isEqualTo("SAVE10");
        assertThat(coupon.isActive()).isTrue();
        assertThat(coupon.getUsedCount()).isZero();
        assertThat(coupon.getCreatedAt()).isNotNull();
    }

    @Test
    void create_percentageOutOfRange_shouldThrow() {
        assertThatThrownBy(() -> buildPercentage("X", 101, null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> buildPercentage("X", 0, null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_fixedAmountNegative_shouldThrow() {
        assertThatThrownBy(() ->
                Coupon.create("X", null, DiscountType.FIXED_AMOUNT,
                        new BigDecimal("-5"), null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_blankCode_shouldThrow() {
        assertThatThrownBy(() -> buildPercentage("  ", 10, null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validate_activeCoupon_shouldNotThrow() {
        Coupon coupon = buildPercentage("SAVE10", 10, null, null, null, null);
        assertThatNoException().isThrownBy(() -> coupon.validate(SUBTOTAL_100));
    }

    @Test
    void validate_inactiveCoupon_shouldThrowCouponInactiveException() {
        Coupon coupon = buildPercentage("SAVE10", 10, null, null, null, null);
        coupon.deactivate();

        assertThatThrownBy(() -> coupon.validate(SUBTOTAL_100))
                .isInstanceOf(CouponInactiveException.class);
    }

    @Test
    void validate_notYetValid_shouldThrowCouponInactiveException() {
        Instant future = Instant.now().plus(1, ChronoUnit.DAYS);
        Coupon coupon = buildPercentage("SAVE10", 10, null, null, future, null);

        assertThatThrownBy(() -> coupon.validate(SUBTOTAL_100))
                .isInstanceOf(CouponInactiveException.class);
    }

    @Test
    void validate_expiredCoupon_shouldThrowCouponExpiredException() {
        Instant past = Instant.now().minus(1, ChronoUnit.DAYS);
        Coupon coupon = buildPercentage("SAVE10", 10, null, null, null, past);

        assertThatThrownBy(() -> coupon.validate(SUBTOTAL_100))
                .isInstanceOf(CouponExpiredException.class);
    }

    @Test
    void validate_maxUsesReached_shouldThrowCouponMaxUsesReachedException() {
        Coupon coupon = buildPercentage("SAVE10", 10, null, 2, null, null);
        coupon.incrementUsage();
        coupon.incrementUsage();

        assertThatThrownBy(() -> coupon.validate(SUBTOTAL_100))
                .isInstanceOf(CouponMaxUsesReachedException.class);
    }

    @Test
    void validate_belowMinimumOrder_throwsCouponMinimumOrderNotMetException() {
        Coupon coupon = buildPercentage("SAVE10", 10, new BigDecimal("50.00"), null, null, null);

        assertThatThrownBy(() -> coupon.validate(SUBTOTAL_30))
                .isInstanceOf(CouponMinimumOrderNotMetException.class);
    }

    @Test
    void validate_exactlyAtMinimumOrder_shouldNotThrow() {
        Coupon coupon = buildPercentage("SAVE10", 10, new BigDecimal("100.00"), null, null, null);
        assertThatNoException().isThrownBy(() -> coupon.validate(SUBTOTAL_100));
    }

    @Test
    void validate_noMaxUses_shouldAlwaysPass() {
        Coupon coupon = buildPercentage("SAVE10", 10, null, null, null, null);
        for (int i = 0; i < 1000; i++) coupon.incrementUsage();

        assertThatNoException().isThrownBy(() -> coupon.validate(SUBTOTAL_100));
    }

    @Test
    void calculateDiscount_percentage_shouldReturnCorrectAmount() {
        Coupon coupon = buildPercentage("SAVE10", 10, null, null, null, null);

        Money discount = coupon.calculateDiscount(SUBTOTAL_100);

        assertThat(discount.amount()).isEqualByComparingTo("10.00");
        assertThat(discount.currency()).isEqualTo("USD");
    }

    @Test
    void calculateDiscount_fixedAmount_shouldReturnDiscountValue() {
        Coupon coupon = Coupon.create("OFF5", null, DiscountType.FIXED_AMOUNT,
                new BigDecimal("5.00"), null, null, null, null);

        Money discount = coupon.calculateDiscount(SUBTOTAL_100);

        assertThat(discount.amount()).isEqualByComparingTo("5.00");
    }

    @Test
    void calculateDiscount_fixedAmountExceedsSubtotal_shouldCapAtSubtotal() {
        Coupon coupon = Coupon.create("OFF200", null, DiscountType.FIXED_AMOUNT,
                new BigDecimal("200.00"), null, null, null, null);

        Money discount = coupon.calculateDiscount(SUBTOTAL_30);

        assertThat(discount.amount()).isEqualByComparingTo("30.00");
    }

    @Test
    void calculateDiscount_percentage50_shouldReturnHalfSubtotal() {
        Coupon coupon = buildPercentage("HALF", 50, null, null, null, null);

        Money discount = coupon.calculateDiscount(SUBTOTAL_100);

        assertThat(discount.amount()).isEqualByComparingTo("50.00");
    }

    @Test
    void deactivate_shouldSetActiveFalse() {
        Coupon coupon = buildPercentage("X", 10, null, null, null, null);
        coupon.deactivate();
        assertThat(coupon.isActive()).isFalse();
    }

    @Test
    void activate_shouldSetActiveTrue() {
        Coupon coupon = buildPercentage("X", 10, null, null, null, null);
        coupon.deactivate();
        coupon.activate();
        assertThat(coupon.isActive()).isTrue();
    }

    @Test
    void incrementUsage_shouldIncreaseUsedCount() {
        Coupon coupon = buildPercentage("X", 10, null, null, null, null);
        coupon.incrementUsage();
        coupon.incrementUsage();
        assertThat(coupon.getUsedCount()).isEqualTo(2);
    }

    private Coupon buildPercentage(String code, int percentage,
                                   BigDecimal minimum, Integer maxUses,
                                   Instant validFrom, Instant validUntil) {
        return Coupon.create(code, "Test coupon", DiscountType.PERCENTAGE,
                new BigDecimal(percentage), minimum, maxUses, validFrom, validUntil);
    }
}
