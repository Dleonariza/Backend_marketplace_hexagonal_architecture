package com.marketplace.hexagonal.Domain.Cart;

import com.marketplace.hexagonal.Domain.Cart.Exceptions.CartAlreadyCheckedOutException;
import com.marketplace.hexagonal.Domain.Cart.Models.*;
import com.marketplace.hexagonal.Domain.Coupon.Exceptions.CouponInactiveException;
import com.marketplace.hexagonal.Domain.Coupon.Exceptions.CouponMinimumOrderNotMetException;
import com.marketplace.hexagonal.Domain.Coupon.Models.Coupon;
import com.marketplace.hexagonal.Domain.Coupon.Models.DiscountType;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class CartCouponTest {

    private static final UserId USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final ProductId PRODUCT_A = ProductId.of("bc66d237-f54d-4bad-b521-0eba3b06304c");
    private static final SellerProfileId SELLER_ID = SellerProfileId.of("770e8400-e29b-41d4-a716-446655440002");

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = Cart.create(USER_ID);
        cart.addItem(PRODUCT_A, null, null, null, SELLER_ID, 2,
                Money.of(new BigDecimal("50.00"), "USD"),
                "Product A", "product-a", null);
    }

    @Test
    void applyCoupon_percentage_shouldStoreSnapshot() {
        Coupon coupon = buildPercentage("SAVE10", 10, null);

        cart.applyCoupon(coupon);

        assertThat(cart.getAppliedCoupon()).isNotNull();
        assertThat(cart.getAppliedCoupon().couponCode()).isEqualTo("SAVE10");
        assertThat(cart.getAppliedCoupon().discountType()).isEqualTo(DiscountType.PERCENTAGE);
        assertThat(cart.getAppliedCoupon().discountAmount().amount())
                .isEqualByComparingTo("10.00");
    }

    @Test
    void applyCoupon_fixed_shouldStoreCorrectDiscount() {
        Coupon coupon = buildFixed("OFF5", new BigDecimal("5.00"));

        cart.applyCoupon(coupon);

        assertThat(cart.getAppliedCoupon().discountAmount().amount())
                .isEqualByComparingTo("5.00");
    }

    @Test
    void applyCoupon_replacesPreviousCoupon() {
        cart.applyCoupon(buildPercentage("FIRST", 10, null));
        cart.applyCoupon(buildPercentage("SECOND", 20, null));

        assertThat(cart.getAppliedCoupon().couponCode()).isEqualTo("SECOND");
        assertThat(cart.getAppliedCoupon().discountAmount().amount())
                .isEqualByComparingTo("20.00");
    }

    @Test
    void applyCoupon_inactiveCoupon_shouldThrow() {
        Coupon coupon = buildPercentage("INACTIVE", 10, null);
        coupon.deactivate();

        assertThatThrownBy(() -> cart.applyCoupon(coupon))
                .isInstanceOf(CouponInactiveException.class);
    }

    @Test
    void applyCoupon_minimumNotMet_shouldThrow() {
        Coupon coupon = buildPercentage("HIGH_MIN", 10, new BigDecimal("200.00"));

        assertThatThrownBy(() -> cart.applyCoupon(coupon))
                .isInstanceOf(CouponMinimumOrderNotMetException.class);
    }

    @Test
    void applyCoupon_toCheckedOutCart_shouldThrow() {
        cart.checkout();
        Coupon coupon = buildPercentage("SAVE10", 10, null);

        assertThatThrownBy(() -> cart.applyCoupon(coupon))
                .isInstanceOf(CartAlreadyCheckedOutException.class);
    }

    @Test
    void removeCoupon_shouldClearAppliedCoupon() {
        cart.applyCoupon(buildPercentage("SAVE10", 10, null));
        cart.removeCoupon();

        assertThat(cart.getAppliedCoupon()).isNull();
    }

    @Test
    void removeCoupon_whenNoCoupon_shouldNotThrow() {
        assertThatNoException().isThrownBy(() -> cart.removeCoupon());
    }

    @Test
    void removeCoupon_toCheckedOutCart_shouldThrow() {
        cart.applyCoupon(buildPercentage("SAVE10", 10, null));
        cart.checkout();

        assertThatThrownBy(() -> cart.removeCoupon())
                .isInstanceOf(CartAlreadyCheckedOutException.class);
    }

    @Test
    void getDiscount_withCoupon_shouldReturnDiscountAmount() {
        cart.applyCoupon(buildPercentage("SAVE10", 10, null));

        assertThat(cart.getDiscount().amount()).isEqualByComparingTo("10.00");
    }

    @Test
    void getDiscount_withoutCoupon_shouldReturnZero() {
        assertThat(cart.getDiscount().amount()).isEqualByComparingTo("0.00");
    }

    @Test
    void getFinalTotal_withPercentageCoupon_returnsSubtotalMinusDiscount() {
        cart.applyCoupon(buildPercentage("SAVE10", 10, null));

        assertThat(cart.getFinalTotal().amount()).isEqualByComparingTo("90.00");
    }

    @Test
    void getFinalTotal_withFixedCoupon_shouldReturnSubtotalMinusFixed() {
        cart.applyCoupon(buildFixed("OFF5", new BigDecimal("5.00")));

        assertThat(cart.getFinalTotal().amount()).isEqualByComparingTo("95.00");
    }

    @Test
    void getFinalTotal_withoutCoupon_shouldEqualSubtotal() {
        assertThat(cart.getFinalTotal().amount())
                .isEqualByComparingTo(cart.getTotal().amount());
    }

    @Test
    void getFinalTotal_discountLargerThanTotal_shouldReturnZero() {
        Coupon bigCoupon = buildFixed("BIG", new BigDecimal("200.00"));
        cart.applyCoupon(bigCoupon);

        assertThat(cart.getFinalTotal().amount()).isEqualByComparingTo("0.00");
    }

    @Test
    void getFinalTotal_emptyCart_shouldReturnZero() {
        Cart emptyCart = Cart.create(USER_ID);
        assertThat(emptyCart.getFinalTotal().amount()).isEqualByComparingTo("0.00");
    }

    private Coupon buildPercentage(String code, int pct, BigDecimal minimum) {
        return Coupon.create(code, "desc", DiscountType.PERCENTAGE,
                new BigDecimal(pct), minimum, null, null, null);
    }

    private Coupon buildFixed(String code, BigDecimal amount) {
        return Coupon.create(code, "desc", DiscountType.FIXED_AMOUNT,
                amount, null, null, null, null);
    }
}
