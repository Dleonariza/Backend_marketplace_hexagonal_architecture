package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Shipping.Services.ShippingService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Shipping.Models.SellerShippingOption;
import com.marketplace.hexagonal.Domain.Shipping.Models.ShippingQuote;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.Exception.GlobalExceptionHandler;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ShippingControllerTest {

    @Mock ShippingService shippingService;
    @Mock UserService userService;
    @InjectMocks ShippingController controller;
    private static final String USER_EMAIL = "user@test.com";
    private static final String SELLER_ID_STR = "770e8400-e29b-41d4-a716-446655440002";
    private static final SellerProfileId SELLER_ID = SellerProfileId.of(SELLER_ID_STR);
    private static final Money PRICE_5 = Money.of(new BigDecimal("5.00"), "USD");

    @AfterEach
    void tearDown() { SecurityContextHolder.clearContext(); }

    private MockMvc buildAuthMvc() {
        var auth = new UsernamePasswordAuthenticationToken(
                USER_EMAIL, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        return MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .addFilter((req, res, chain) -> {
                    chain.doFilter(new HttpServletRequestWrapper((HttpServletRequest) req) {
                        @Override public java.security.Principal getUserPrincipal() {
                            Authentication a = SecurityContextHolder.getContext().getAuthentication();
                            return a;
                        }
                    }, res);
                })
                .build();
    }

    private MockMvc buildAnonMvc() {
        SecurityContextHolder.clearContext();
        return MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getShipping_authenticated_returnsQuotes() throws Exception {
        MockMvc mvc = buildAuthMvc();

        UserModel user = UserModel.create(USER_EMAIL, "u1", "hash", "User", null, "300", List.of());
        when(userService.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

        ShippingQuote quote = new ShippingQuote(
                SELLER_ID_STR, "My Store",
                Money.of(new BigDecimal("100.00"), "USD"),
                List.of(buildOption("Standard", 3, 5)));

        when(shippingService.calculateShipping(any(UserId.class), isNull()))
                .thenReturn(List.of(quote));

        mvc.perform(get("/checkout/shipping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].storeName").value("My Store"))
                .andExpect(jsonPath("$[0].itemsSubtotal").value(100.00))
                .andExpect(jsonPath("$[0].currency").value("USD"))
                .andExpect(jsonPath("$[0].options.length()").value(1))
                .andExpect(jsonPath("$[0].options[0].name").value("Standard"));
    }

    @Test
    void getShipping_authenticated_emptyCart_returnsEmpty() throws Exception {
        MockMvc mvc = buildAuthMvc();

        UserModel user = UserModel.create(USER_EMAIL, "u1", "hash", "User", null, "300", List.of());
        when(userService.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(shippingService.calculateShipping(any(UserId.class), isNull())).thenReturn(List.of());

        mvc.perform(get("/checkout/shipping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getShipping_authenticated_multipleSellers_returnsMultipleQuotes() throws Exception {
        MockMvc mvc = buildAuthMvc();

        UserModel user = UserModel.create(USER_EMAIL, "u1", "hash", "User", null, "300", List.of());
        when(userService.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

        SellerProfileId seller2 = SellerProfileId.of("880e8400-e29b-41d4-a716-446655440003");
        ShippingQuote q1 = new ShippingQuote(SELLER_ID_STR, "Store One",
                Money.of(new BigDecimal("50.00"), "USD"),
                List.of(buildOption("Standard", 3, 5), buildOption("Express", 1, 2)));
        ShippingQuote q2 = new ShippingQuote(seller2.getValue(), "Store Two",
                Money.of(new BigDecimal("75.00"), "USD"), List.of());

        when(shippingService.calculateShipping(any(UserId.class), isNull()))
                .thenReturn(List.of(q1, q2));

        mvc.perform(get("/checkout/shipping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].storeName").value("Store One"))
                .andExpect(jsonPath("$[0].options.length()").value(2))
                .andExpect(jsonPath("$[1].storeName").value("Store Two"))
                .andExpect(jsonPath("$[1].options.length()").value(0));
    }

    @Test
    void getShipping_anonymous_withCookie_returnsQuotes() throws Exception {
        MockMvc mvc = buildAnonMvc();

        ShippingQuote quote = new ShippingQuote(SELLER_ID_STR, "My Store",
                Money.of(new BigDecimal("30.00"), "USD"),
                List.of(buildOption("Standard", 3, 5)));

        when(shippingService.calculateShipping(isNull(), any()))
                .thenReturn(List.of(quote));

        mvc.perform(get("/checkout/shipping")
                        .cookie(new Cookie("CART_SESSION", "test-session")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].itemsSubtotal").value(30.00));
    }

    @Test
    void getShipping_anonymous_noCookie_returnsEmpty() throws Exception {
        MockMvc mvc = buildAnonMvc();

        when(shippingService.calculateShipping(isNull(), isNull())).thenReturn(List.of());

        mvc.perform(get("/checkout/shipping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private SellerShippingOption buildOption(String name, int min, int max) {
        return SellerShippingOption.create(SELLER_ID, name, null, PRICE_5, min, max);
    }
}
