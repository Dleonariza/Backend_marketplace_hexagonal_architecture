package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.hexagonal.Application.Order.Services.OrderService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Order.Exceptions.SubOrderAccessDeniedException;
import com.marketplace.hexagonal.Domain.Order.Exceptions.SubOrderNotFoundException;
import com.marketplace.hexagonal.Domain.Order.Models.*;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SellerOrderControllerTest {

    @Mock OrderService orderService;
    @Mock UserService userService;
    @Mock SellerProfileRepositoryPort sellerRepo;
    @InjectMocks SellerOrderController controller;
    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    private static final String SELLER_EMAIL = "seller@test.com";
    private static final UserId USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final SellerProfileId SELLER_ID = SellerProfileId.of("770e8400-e29b-41d4-a716-446655440002");
    private static final Money ZERO = Money.of(BigDecimal.ZERO, "USD");

    @BeforeEach
    void setUp() {
        var auth = new UsernamePasswordAuthenticationToken(
                SELLER_EMAIL, null, List.of(new SimpleGrantedAuthority("ROLE_SELLER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        UserModel seller = UserModel.create(SELLER_EMAIL, "seller1", "hash", "Seller", null, "300", List.of());
        org.mockito.Mockito.lenient()
                .when(userService.findByEmail(SELLER_EMAIL)).thenReturn(Optional.of(seller));

        SellerProfile profile = org.mockito.Mockito.mock(SellerProfile.class);
        org.mockito.Mockito.lenient().when(profile.getId()).thenReturn(SELLER_ID);
        org.mockito.Mockito.lenient()
                .when(sellerRepo.findByUserId(any())).thenReturn(Optional.of(profile));

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
    void listSubOrders_returns200WithList() throws Exception {
        SubOrder sub1 = buildSubOrder();
        SubOrder sub2 = buildSubOrder();
        when(orderService.getSellerSubOrders(SELLER_ID)).thenReturn(List.of(sub1, sub2));

        mockMvc.perform(get("/seller/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].sellerId").value(SELLER_ID.getValue()))
                .andExpect(jsonPath("$[0].status").value("PENDING_PAYMENT"));
    }

    @Test
    void listSubOrders_empty_returnsEmptyArray() throws Exception {
        when(orderService.getSellerSubOrders(SELLER_ID)).thenReturn(List.of());

        mockMvc.perform(get("/seller/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getSubOrder_found_returns200() throws Exception {
        SubOrder sub = buildSubOrder();
        when(orderService.getSellerSubOrders(SELLER_ID)).thenReturn(List.of(sub));

        mockMvc.perform(get("/seller/orders/" + sub.getId().getValue()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sub.getId().getValue()))
                .andExpect(jsonPath("$.sellerId").value(SELLER_ID.getValue()));
    }

    @Test
    void getSubOrder_notFound_returns404() throws Exception {
        when(orderService.getSellerSubOrders(SELLER_ID)).thenReturn(List.of());

        mockMvc.perform(get("/seller/orders/" + SubOrderId.generate().getValue()))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStatus_validTransition_returns200() throws Exception {
        SubOrder sub = buildSubOrder();
        sub.markPaid();
        SubOrder updated = buildSubOrder();
        updated.markPaid();
        updated.markProcessing();

        when(orderService.updateSubOrderStatus(eq(SELLER_ID), eq(sub.getId().getValue()),
                eq(SubOrderStatus.PROCESSING))).thenReturn(updated);

        mockMvc.perform(patch("/seller/orders/" + sub.getId().getValue() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"PROCESSING"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSING"));
    }

    @Test
    void updateStatus_nullStatus_returns400() throws Exception {
        SubOrder sub = buildSubOrder();

        mockMvc.perform(patch("/seller/orders/" + sub.getId().getValue() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_subOrderNotFound_returns404() throws Exception {
        String id = SubOrderId.generate().getValue();
        when(orderService.updateSubOrderStatus(any(), eq(id), any()))
                .thenThrow(new SubOrderNotFoundException(id));

        mockMvc.perform(patch("/seller/orders/" + id + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"PROCESSING"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStatus_wrongSeller_returns403() throws Exception {
        String id = SubOrderId.generate().getValue();
        when(orderService.updateSubOrderStatus(any(), eq(id), any()))
                .thenThrow(new SubOrderAccessDeniedException());

        mockMvc.perform(patch("/seller/orders/" + id + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"PROCESSING"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateStatus_illegalTransition_returns409() throws Exception {
        String id = SubOrderId.generate().getValue();
        when(orderService.updateSubOrderStatus(any(), eq(id), any()))
                .thenThrow(new IllegalStateException("Invalid transition."));

        mockMvc.perform(patch("/seller/orders/" + id + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"SHIPPED"}
                                """))
                .andExpect(status().isConflict());
    }

    private SubOrder buildSubOrder() {
        Cart cart = Cart.create(USER_ID);
        cart.addItem(ProductId.of("bc66d237-f54d-4bad-b521-0eba3b06304c"),
                null, null, null, SELLER_ID, 1,
                Money.of(new BigDecimal("50.00"), "USD"), "Product", "product", null);

        return SubOrder.create(OrderId.generate(), SELLER_ID, "Test Store",
                cart.getItems(), ZERO, null);
    }
}
