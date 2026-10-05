package com.marketplace.hexagonal.Infrastructura.Controllers;

import tools.jackson.databind.ObjectMapper;
import com.marketplace.hexagonal.Application.Cart.Services.CartService;
import com.marketplace.hexagonal.Application.Coupon.Services.CouponService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Cart.Exceptions.CartItemNotFoundException;
import com.marketplace.hexagonal.Domain.Cart.Exceptions.InsufficientStockException;
import com.marketplace.hexagonal.Domain.Cart.Models.*;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock CartService cartService;
    @Mock CouponService couponService;
    @Mock UserService userService;
    @InjectMocks CartController cartController;
    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    private static final String USER_EMAIL = "user@test.com";
    private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final String PRODUCT_ID = "bc66d237-f54d-4bad-b521-0eba3b06304c";
    private static final String ITEM_ID = "dd66d237-f54d-4bad-b521-0eba3b06304c";

    @BeforeEach
    void setUp() {
        var auth = new UsernamePasswordAuthenticationToken(
                USER_EMAIL, null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        UserModel user = UserModel.create(USER_EMAIL, "user1", "hash", "User", null, "3001234567", List.of());
        org.mockito.Mockito.lenient()
                .when(userService.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(user));

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(cartController)
                .setControllerAdvice(new GlobalExceptionHandler())
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
    void getCart_shouldReturn200WithCartData() throws Exception {
        Cart cart = buildCart();
        when(cartService.getOrCreateCart(any())).thenReturn(cart);

        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").isNotEmpty())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void addItem_withValidRequest_shouldReturn201() throws Exception {
        Cart cart = buildCartWithItem();
        when(cartService.addToCart(any(), isNull(), eq(PRODUCT_ID), isNull(), eq(2))).thenReturn(cart);

        String body = """
                {"productId":"%s","variantId":null,"quantity":2}
                """.formatted(PRODUCT_ID);

        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items[0].productId").value(PRODUCT_ID));
    }

    @Test
    void addItem_withInsufficientStock_shouldReturn409() throws Exception {
        when(cartService.addToCart(any(), any(), any(), any(), anyInt()))
                .thenThrow(new InsufficientStockException("Only 1 unit(s) available"));

        String body = """
                {"productId":"%s","variantId":null,"quantity":99}
                """.formatted(PRODUCT_ID);

        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void addItem_withZeroQuantity_shouldReturn400() throws Exception {
        String body = """
                {"productId":"%s","variantId":null,"quantity":0}
                """.formatted(PRODUCT_ID);

        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateQuantity_shouldReturn200() throws Exception {
        Cart cart = buildCartWithItem();
        when(cartService.updateQuantity(any(), isNull(), eq(ITEM_ID), eq(5))).thenReturn(cart);

        mockMvc.perform(put("/cart/items/" + ITEM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":5}"))
                .andExpect(status().isOk());
    }

    @Test
    void removeItem_shouldReturn200() throws Exception {
        Cart cart = buildCart();
        when(cartService.removeFromCart(any(), isNull(), eq(ITEM_ID))).thenReturn(cart);

        mockMvc.perform(delete("/cart/items/" + ITEM_ID))
                .andExpect(status().isOk());
    }

    @Test
    void removeItem_nonExistentItem_shouldReturn404() throws Exception {
        when(cartService.removeFromCart(any(), any(), any()))
                .thenThrow(new CartItemNotFoundException("Item not found"));

        mockMvc.perform(delete("/cart/items/" + ITEM_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void clearCart_shouldReturn200WithEmptyItems() throws Exception {
        Cart emptyCart = buildCart();
        when(cartService.clearCart(any(), isNull())).thenReturn(emptyCart);

        mockMvc.perform(delete("/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    private Cart buildCart() {
        return Cart.reconstitute(
                CartId.of("ff66d237-f54d-4bad-b521-0eba3b06304c"),
                UserId.of(USER_ID),
                null,
                List.of(),
                CartStatus.ACTIVE, null,
                Instant.now(), Instant.now(),
                Instant.now().plus(Cart.TTL_DAYS, java.time.temporal.ChronoUnit.DAYS));
    }

    private Cart buildCartWithItem() {
        Cart cart = buildCart();
        cart.addItem(
                ProductId.of(PRODUCT_ID), null, null, null,
                SellerProfileId.of("770e8400-e29b-41d4-a716-446655440002"),
                2, Money.of(new BigDecimal("50.00"), "USD"),
                "Test Product", "test-product", null);
        return cart;
    }
}
