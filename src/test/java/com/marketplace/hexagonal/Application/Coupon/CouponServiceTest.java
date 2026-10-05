package com.marketplace.hexagonal.Application.Coupon;

import com.marketplace.hexagonal.Application.Coupon.Services.CouponService;
import com.marketplace.hexagonal.Application.Coupon.UseCases.*;
import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Cart.Models.CartStatus;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.CartRepositoryPort;
import com.marketplace.hexagonal.Domain.Coupon.Exceptions.CouponNotFoundException;
import com.marketplace.hexagonal.Domain.Coupon.Models.Coupon;
import com.marketplace.hexagonal.Domain.Coupon.Models.DiscountType;
import com.marketplace.hexagonal.Domain.Coupon.Ports.Out.CouponRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock CouponRepositoryPort couponRepo;
    @Mock CartRepositoryPort cartRepo;
    CouponService couponService;

    private static final UserId USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final ProductId PRODUCT_ID = ProductId.of("bc66d237-f54d-4bad-b521-0eba3b06304c");
    private static final SellerProfileId SELLER = SellerProfileId.of("770e8400-e29b-41d4-a716-446655440002");

    @BeforeEach
    void setUp() {
        couponService = new CouponService(
                new CreateCouponUseCaseImpl(couponRepo),
                new GetCouponUseCaseImpl(couponRepo),
                new ListCouponsUseCaseImpl(couponRepo),
                new ToggleCouponUseCaseImpl(couponRepo),
                new ApplyCouponUseCaseImpl(cartRepo, couponRepo),
                new RemoveCouponUseCaseImpl(cartRepo)
        );
    }

    @Test
    void createCoupon_shouldSaveAndReturnCoupon() {
        Coupon saved = buildPercentageCoupon("SAVE10", 10);
        when(couponRepo.save(any())).thenReturn(saved);

        Coupon result = couponService.createCoupon(
                "SAVE10", "desc", DiscountType.PERCENTAGE,
                new BigDecimal("10"), null, null, null, null);

        assertThat(result.getCode()).isEqualTo("SAVE10");
        verify(couponRepo).save(any());
    }

    @Test
    void getCouponByCode_existingCode_shouldReturnCoupon() {
        Coupon coupon = buildPercentageCoupon("SAVE10", 10);
        when(couponRepo.findByCode("SAVE10")).thenReturn(Optional.of(coupon));

        Coupon result = couponService.getCouponByCode("SAVE10");

        assertThat(result.getCode()).isEqualTo("SAVE10");
    }

    @Test
    void getCouponByCode_notFound_shouldThrowCouponNotFoundException() {
        when(couponRepo.findByCode(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.getCouponByCode("UNKNOWN"))
                .isInstanceOf(CouponNotFoundException.class);
    }

    @Test
    void listAllCoupons_shouldReturnAllCoupons() {
        when(couponRepo.findAll()).thenReturn(List.of(
                buildPercentageCoupon("SAVE10", 10),
                buildPercentageCoupon("SAVE20", 20)));

        List<Coupon> result = couponService.listAllCoupons();

        assertThat(result).hasSize(2);
    }

    @Test
    void listAllCoupons_empty_shouldReturnEmptyList() {
        when(couponRepo.findAll()).thenReturn(List.of());

        assertThat(couponService.listAllCoupons()).isEmpty();
    }

    @Test
    void deactivateCoupon_shouldSetInactiveAndSave() {
        Coupon coupon = buildPercentageCoupon("SAVE10", 10);
        when(couponRepo.findByCode("SAVE10")).thenReturn(Optional.of(coupon));
        when(couponRepo.save(any())).thenReturn(coupon);

        couponService.deactivateCoupon("SAVE10");

        assertThat(coupon.isActive()).isFalse();
        verify(couponRepo).save(coupon);
    }

    @Test
    void activateCoupon_shouldSetActiveAndSave() {
        Coupon coupon = buildPercentageCoupon("SAVE10", 10);
        coupon.deactivate();
        when(couponRepo.findByCode("SAVE10")).thenReturn(Optional.of(coupon));
        when(couponRepo.save(any())).thenReturn(coupon);

        couponService.activateCoupon("SAVE10");

        assertThat(coupon.isActive()).isTrue();
        verify(couponRepo).save(coupon);
    }

    @Test
    void toggleCoupon_notFound_shouldThrow() {
        when(couponRepo.findByCode(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.activateCoupon("UNKNOWN"))
                .isInstanceOf(CouponNotFoundException.class);
        assertThatThrownBy(() -> couponService.deactivateCoupon("UNKNOWN"))
                .isInstanceOf(CouponNotFoundException.class);
    }

    @Test
    void applyCoupon_authenticatedUser_shouldApplyAndSaveCart() {
        Cart cart = cartWithItem();
        Coupon coupon = buildPercentageCoupon("SAVE10", 10);

        when(cartRepo.findActiveByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(couponRepo.findByCode("SAVE10")).thenReturn(Optional.of(coupon));
        when(cartRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Cart result = couponService.applyCoupon(USER_ID, null, "SAVE10");

        assertThat(result.getAppliedCoupon()).isNotNull();
        assertThat(result.getAppliedCoupon().couponCode()).isEqualTo("SAVE10");
        verify(cartRepo).save(any());
    }

    @Test
    void applyCoupon_anonymousSession_shouldApplyAndSaveCart() {
        Cart cart = cartWithItem();
        Coupon coupon = buildPercentageCoupon("SAVE10", 10);
        String sessionId = "test-session-id";

        when(cartRepo.findActiveBySessionId(sessionId)).thenReturn(Optional.of(cart));
        when(couponRepo.findByCode("SAVE10")).thenReturn(Optional.of(coupon));
        when(cartRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Cart result = couponService.applyCoupon(null, sessionId, "SAVE10");

        assertThat(result.getAppliedCoupon()).isNotNull();
        verify(cartRepo).save(any());
    }

    @Test
    void applyCoupon_couponNotFound_shouldThrow() {
        Cart cart = cartWithItem();
        when(cartRepo.findActiveByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(couponRepo.findByCode(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.applyCoupon(USER_ID, null, "INVALID"))
                .isInstanceOf(CouponNotFoundException.class);
    }

    @Test
    void applyCoupon_noCartExists_shouldCreateNewCartAndApply() {
        Coupon coupon = buildPercentageCoupon("SAVE10", 10);
        Cart newCart = Cart.create(USER_ID);
        newCart.addItem(PRODUCT_ID, null, null, null, SELLER, 1,
                Money.of(new BigDecimal("100.00"), "USD"), "P", "p", null);

        when(cartRepo.findActiveByUserId(USER_ID)).thenReturn(Optional.empty());
        when(cartRepo.save(any())).thenReturn(newCart);
        when(couponRepo.findByCode("SAVE10")).thenReturn(Optional.of(coupon));

        when(cartRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThatNoException().isThrownBy(() ->
                couponService.applyCoupon(USER_ID, null, "SAVE10"));
    }

    @Test
    void removeCoupon_shouldClearCouponAndSave() {
        Cart cart = cartWithItem();
        Coupon coupon = buildPercentageCoupon("SAVE10", 10);
        cart.applyCoupon(coupon);

        when(cartRepo.findActiveByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(cartRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Cart result = couponService.removeCoupon(USER_ID, null);

        assertThat(result.getAppliedCoupon()).isNull();
        verify(cartRepo).save(any());
    }

    @Test
    void removeCoupon_anonymousSession_shouldClearCoupon() {
        Cart cart = cartWithItem();
        cart.applyCoupon(buildPercentageCoupon("SAVE10", 10));
        String sessionId = "test-session-id";

        when(cartRepo.findActiveBySessionId(sessionId)).thenReturn(Optional.of(cart));
        when(cartRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Cart result = couponService.removeCoupon(null, sessionId);

        assertThat(result.getAppliedCoupon()).isNull();
    }

    private Coupon buildPercentageCoupon(String code, int pct) {
        return Coupon.create(code, "desc", DiscountType.PERCENTAGE,
                new BigDecimal(pct), null, null, null, null);
    }

    private Cart cartWithItem() {
        Cart cart = Cart.create(USER_ID);
        cart.addItem(PRODUCT_ID, null, null, null, SELLER, 2,
                Money.of(new BigDecimal("50.00"), "USD"),
                "Product", "product", null);
        return cart;
    }
}
