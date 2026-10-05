package com.marketplace.hexagonal.Infrastructura.Controllers;

import tools.jackson.databind.ObjectMapper;
import com.marketplace.hexagonal.Application.Cart.Services.CartService;
import com.marketplace.hexagonal.Application.Coupon.Services.CouponService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Cart.Models.CartId;
import com.marketplace.hexagonal.Domain.Cart.Models.CartStatus;
import com.marketplace.hexagonal.Domain.Coupon.Exceptions.CouponInactiveException;
import com.marketplace.hexagonal.Domain.Coupon.Exceptions.CouponMinimumOrderNotMetException;
import com.marketplace.hexagonal.Domain.Coupon.Exceptions.CouponNotFoundException;
import com.marketplace.hexagonal.Domain.Coupon.Models.Coupon;
import com.marketplace.hexagonal.Domain.Coupon.Models.DiscountType;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.Exception.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CouponControllerTest {

    @Mock CartService cartService;
    @Mock CouponService couponService;
    @Mock UserService userService;
    @InjectMocks CouponController couponController;
    @InjectMocks CartController cartController;
    MockMvc couponMvc;
    MockMvc cartMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    private static final String USER_EMAIL = "user@test.com";
    private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final String PRODUCT_ID = "bc66d237-f54d-4bad-b521-0eba3b06304c";

    @BeforeEach
    void setUp() {
        var auth = new UsernamePasswordAuthenticationToken(
                USER_EMAIL, null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        UserModel user = UserModel.create(USER_EMAIL, "user1", "hash", "User", null, "300", List.of());
        org.mockito.Mockito.lenient()
                .when(userService.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(user));

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        couponMvc = MockMvcBuilders
                .standaloneSetup(couponController)
                .setControllerAdvice(handler)
                .setValidator(validator)
                .build();

        cartMvc = MockMvcBuilders
                .standaloneSetup(cartController)
                .setControllerAdvice(handler)
                .setValidator(validator)
                .addFilter((req, res, chain) -> {
                    chain.doFilter(new HttpServletRequestWrapper((HttpServletRequest) req) {
                        @Override
                        public java.security.Principal getUserPrincipal() {
                            Authentication a = SecurityContextHolder.getContext().getAuthentication();
                            return a;
                        }
                    }, res);
                })
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createCoupon_validRequest_shouldReturn201() throws Exception {
        when(couponService.createCoupon(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(buildCoupon("SAVE10", 10));

        String body = """
                {
                  "code": "SAVE10",
                  "description": "10 percent off",
                  "discountType": "PERCENTAGE",
                  "discountValue": 10
                }
                """;

        couponMvc.perform(post("/admin/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("SAVE10"))
                .andExpect(jsonPath("$.discountType").value("PERCENTAGE"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void createCoupon_missingCode_shouldReturn400() throws Exception {
        couponMvc.perform(post("/admin/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"discountType":"PERCENTAGE","discountValue":10}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCoupon_missingDiscountType_shouldReturn400() throws Exception {
        couponMvc.perform(post("/admin/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"SAVE10","discountValue":10}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listCoupons_shouldReturn200WithList() throws Exception {
        when(couponService.listAllCoupons())
                .thenReturn(List.of(buildCoupon("SAVE10", 10), buildCoupon("SAVE20", 20)));

        couponMvc.perform(get("/admin/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].code").value("SAVE10"))
                .andExpect(jsonPath("$[1].code").value("SAVE20"));
    }

    @Test
    void listCoupons_empty_shouldReturn200WithEmptyArray() throws Exception {
        when(couponService.listAllCoupons()).thenReturn(List.of());

        couponMvc.perform(get("/admin/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getCouponByCode_existingCode_shouldReturn200() throws Exception {
        when(couponService.getCouponByCode("SAVE10")).thenReturn(buildCoupon("SAVE10", 10));

        couponMvc.perform(get("/admin/coupons/SAVE10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SAVE10"));
    }

    @Test
    void getCouponByCode_notFound_shouldReturn404() throws Exception {
        when(couponService.getCouponByCode("UNKNOWN"))
                .thenThrow(new CouponNotFoundException("UNKNOWN"));

        couponMvc.perform(get("/admin/coupons/UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void activateCoupon_shouldReturn204() throws Exception {
        couponMvc.perform(patch("/admin/coupons/SAVE10/activate"))
                .andExpect(status().isNoContent());
    }

    @Test
    void activateCoupon_notFound_shouldReturn404() throws Exception {
        doThrow(new CouponNotFoundException("X")).when(couponService).activateCoupon("X");

        couponMvc.perform(patch("/admin/coupons/X/activate"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deactivateCoupon_shouldReturn204() throws Exception {
        couponMvc.perform(patch("/admin/coupons/SAVE10/deactivate"))
                .andExpect(status().isNoContent());
    }

    @Test
    void applyCoupon_validCoupon_shouldReturn200WithDiscount() throws Exception {
        when(couponService.applyCoupon(any(UserId.class), isNull(), eq("SAVE10")))
                .thenReturn(cartWithCoupon());

        cartMvc.perform(post("/cart/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"couponCode":"SAVE10"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appliedCoupon.couponCode").value("SAVE10"))
                .andExpect(jsonPath("$.discount").value(10.00))
                .andExpect(jsonPath("$.total").value(90.00));
    }

    @Test
    void applyCoupon_invalidCode_shouldReturn404() throws Exception {
        when(couponService.applyCoupon(any(UserId.class), isNull(), eq("INVALID")))
                .thenThrow(new CouponNotFoundException("INVALID"));

        cartMvc.perform(post("/cart/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"couponCode":"INVALID"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void applyCoupon_inactiveCoupon_shouldReturn422() throws Exception {
        when(couponService.applyCoupon(any(UserId.class), isNull(), eq("INACTIVE")))
                .thenThrow(new CouponInactiveException("INACTIVE"));

        cartMvc.perform(post("/cart/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"couponCode":"INACTIVE"}
                                """))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void applyCoupon_minimumNotMet_shouldReturn422() throws Exception {
        when(couponService.applyCoupon(any(UserId.class), isNull(), eq("HIGHMIN")))
                .thenThrow(new CouponMinimumOrderNotMetException(
                        "HIGHMIN", new BigDecimal("200.00"), new BigDecimal("100.00")));

        cartMvc.perform(post("/cart/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"couponCode":"HIGHMIN"}
                                """))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void applyCoupon_missingBody_shouldReturn400() throws Exception {
        cartMvc.perform(post("/cart/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeCoupon_shouldReturn200WithNullAppliedCoupon() throws Exception {
        when(couponService.removeCoupon(any(UserId.class), isNull()))
                .thenReturn(buildEmptyCart());

        cartMvc.perform(delete("/cart/coupon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appliedCoupon").doesNotExist());
    }

    private Coupon buildCoupon(String code, int pct) {
        return Coupon.create(code, "desc", DiscountType.PERCENTAGE,
                new BigDecimal(pct), null, null, null, null);
    }

    private Cart cartWithCoupon() {
        Cart cart = Cart.create(UserId.of(USER_ID));
        cart.addItem(
                ProductId.of(PRODUCT_ID), null, null, null,
                SellerProfileId.of("770e8400-e29b-41d4-a716-446655440002"),
                2, Money.of(new BigDecimal("50.00"), "USD"),
                "Product", "product", null);
        cart.applyCoupon(buildCoupon("SAVE10", 10));
        return cart;
    }

    private Cart buildEmptyCart() {
        return Cart.reconstitute(
                CartId.of("ff66d237-f54d-4bad-b521-0eba3b06304c"),
                UserId.of(USER_ID),
                null, List.of(),
                CartStatus.ACTIVE, null,
                Instant.now(), Instant.now(),
                Instant.now().plusSeconds(604800));
    }
}
