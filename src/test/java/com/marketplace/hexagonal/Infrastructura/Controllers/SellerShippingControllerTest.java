package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.hexagonal.Application.Shipping.Services.ShippingService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.Shipping.Exceptions.ShippingOptionNotFoundException;
import com.marketplace.hexagonal.Domain.Shipping.Exceptions.ShippingOptionOwnershipException;
import com.marketplace.hexagonal.Domain.Shipping.Models.SellerShippingOption;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SellerShippingControllerTest {

    @Mock ShippingService shippingService;
    @Mock UserService userService;
    @Mock SellerProfileRepositoryPort sellerRepo;
    @InjectMocks SellerShippingController controller;
    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    private static final String SELLER_EMAIL = "seller@test.com";
    private static final String SELLER_ID_STR = "770e8400-e29b-41d4-a716-446655440002";
    private static final SellerProfileId SELLER_ID = SellerProfileId.of(SELLER_ID_STR);
    private static final Money PRICE_5 = Money.of(new BigDecimal("5.00"), "USD");
    private static final String OPTION_ID = "aa66d237-f54d-4bad-b521-0eba3b06304c";

    @BeforeEach
    void setUp() {
        var auth = new UsernamePasswordAuthenticationToken(
                SELLER_EMAIL, null,
                List.of(new SimpleGrantedAuthority("ROLE_SELLER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        UserModel user = UserModel.create(SELLER_EMAIL, "seller1", "hash", "Seller", null, "300", List.of());
        org.mockito.Mockito.lenient()
                .when(userService.findByEmail(SELLER_EMAIL))
                .thenReturn(Optional.of(user));

        org.mockito.Mockito.lenient()
                .when(sellerRepo.findByUserId(any()))
                .thenReturn(Optional.of(SellerProfile.builder().id(SELLER_ID).storeName("My Store").build()));

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
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

    @AfterEach
    void tearDown() { SecurityContextHolder.clearContext(); }

    @Test
    void addOption_validRequest_shouldReturn201() throws Exception {
        SellerShippingOption saved = buildOption("Standard", 3, 5);
        when(shippingService.addShippingOption(any(), eq("Standard"), any(), any(), eq(3), eq(5)))
                .thenReturn(saved);

        mockMvc.perform(post("/seller/shipping-options")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Standard","description":"3-5 days",
                                 "price":5.00,"currency":"USD",
                                 "estimatedDaysMin":3,"estimatedDaysMax":5}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Standard"))
                .andExpect(jsonPath("$.price").value(5.00))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void addOption_missingName_shouldReturn400() throws Exception {
        mockMvc.perform(post("/seller/shipping-options")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"price":5.00,"currency":"USD",
                                 "estimatedDaysMin":1,"estimatedDaysMax":3}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addOption_negativePrice_shouldReturn400() throws Exception {
        mockMvc.perform(post("/seller/shipping-options")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Standard","price":-1.00,"currency":"USD",
                                 "estimatedDaysMin":1,"estimatedDaysMax":3}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addOption_domainValidationFails_shouldReturn400() throws Exception {
        when(shippingService.addShippingOption(any(), any(), any(), any(), any(Integer.class), any(Integer.class)))
                .thenThrow(new IllegalArgumentException("Max days must be >= min days"));

        mockMvc.perform(post("/seller/shipping-options")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Bad","price":5.00,"currency":"USD",
                                 "estimatedDaysMin":5,"estimatedDaysMax":1}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listOptions_shouldReturn200WithList() throws Exception {
        when(shippingService.listShippingOptions(any())).thenReturn(List.of(
                buildOption("Standard", 3, 5),
                buildOption("Express", 1, 2)));

        mockMvc.perform(get("/seller/shipping-options"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Standard"))
                .andExpect(jsonPath("$[1].name").value("Express"));
    }

    @Test
    void listOptions_empty_shouldReturn200WithEmptyArray() throws Exception {
        when(shippingService.listShippingOptions(any())).thenReturn(List.of());

        mockMvc.perform(get("/seller/shipping-options"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void updateOption_ownerSeller_shouldReturn200() throws Exception {
        SellerShippingOption updated = buildOption("Express", 1, 2);
        when(shippingService.updateShippingOption(any(), eq(OPTION_ID), eq("Express"), any(), any(), eq(1), eq(2)))
                .thenReturn(updated);

        mockMvc.perform(put("/seller/shipping-options/" + OPTION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Express","price":5.00,"currency":"USD",
                                 "estimatedDaysMin":1,"estimatedDaysMax":2}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Express"));
    }

    @Test
    void updateOption_notFound_shouldReturn404() throws Exception {
        when(shippingService.updateShippingOption(any(), any(), any(), any(), any(), any(Integer.class), any(Integer.class)))
                .thenThrow(new ShippingOptionNotFoundException(OPTION_ID));

        mockMvc.perform(put("/seller/shipping-options/" + OPTION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"X","price":5.00,"currency":"USD",
                                 "estimatedDaysMin":1,"estimatedDaysMax":2}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateOption_differentSeller_shouldReturn403() throws Exception {
        when(shippingService.updateShippingOption(any(), any(), any(), any(), any(), any(Integer.class), any(Integer.class)))
                .thenThrow(new ShippingOptionOwnershipException());

        mockMvc.perform(put("/seller/shipping-options/" + OPTION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Hack","price":0.00,"currency":"USD",
                                 "estimatedDaysMin":1,"estimatedDaysMax":2}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void removeOption_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/seller/shipping-options/" + OPTION_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeOption_notFound_shouldReturn404() throws Exception {
        doThrow(new ShippingOptionNotFoundException(OPTION_ID))
                .when(shippingService).removeShippingOption(any(), eq(OPTION_ID));

        mockMvc.perform(delete("/seller/shipping-options/" + OPTION_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeOption_differentSeller_shouldReturn403() throws Exception {
        doThrow(new ShippingOptionOwnershipException())
                .when(shippingService).removeShippingOption(any(), eq(OPTION_ID));

        mockMvc.perform(delete("/seller/shipping-options/" + OPTION_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    void activateOption_shouldReturn204() throws Exception {
        mockMvc.perform(patch("/seller/shipping-options/" + OPTION_ID + "/activate"))
                .andExpect(status().isNoContent());
    }

    @Test
    void activateOption_notFound_shouldReturn404() throws Exception {
        doThrow(new ShippingOptionNotFoundException(OPTION_ID))
                .when(shippingService).activateShippingOption(any(), eq(OPTION_ID));

        mockMvc.perform(patch("/seller/shipping-options/" + OPTION_ID + "/activate"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deactivateOption_shouldReturn204() throws Exception {
        mockMvc.perform(patch("/seller/shipping-options/" + OPTION_ID + "/deactivate"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deactivateOption_notFound_shouldReturn404() throws Exception {
        doThrow(new ShippingOptionNotFoundException(OPTION_ID))
                .when(shippingService).deactivateShippingOption(any(), eq(OPTION_ID));

        mockMvc.perform(patch("/seller/shipping-options/" + OPTION_ID + "/deactivate"))
                .andExpect(status().isNotFound());
    }

    @Test
    void anyRequest_sellerProfileNotFound_shouldReturn404() throws Exception {
        when(sellerRepo.findByUserId(any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/seller/shipping-options"))
                .andExpect(status().isNotFound());
    }

    private SellerShippingOption buildOption(String name, int min, int max) {
        return SellerShippingOption.create(SELLER_ID, name, null, PRICE_5, min, max);
    }
}
