package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.hexagonal.Application.Order.Services.OrderService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Order.Exceptions.EmptyCartException;
import com.marketplace.hexagonal.Domain.Order.Exceptions.OrderAccessDeniedException;
import com.marketplace.hexagonal.Domain.Order.Exceptions.OrderCancellationNotAllowedException;
import com.marketplace.hexagonal.Domain.Order.Exceptions.OrderNotFoundException;
import com.marketplace.hexagonal.Domain.Order.Models.*;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock OrderService orderService;
    @Mock UserService userService;
    @InjectMocks OrderController controller;
    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    private static final String USER_EMAIL = "buyer@test.com";
    private static final UserId USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final SellerProfileId SELLER_ID = SellerProfileId.of("770e8400-e29b-41d4-a716-446655440002");
    private static final String IDEM_KEY = "550e8400-e29b-41d4-a716-111111111111";
    private static final String ADDR_ID = "660e8400-e29b-41d4-a716-222222222222";
    private static final Money ZERO = Money.of(BigDecimal.ZERO, "USD");

    @BeforeEach
    void setUp() {
        var auth = new UsernamePasswordAuthenticationToken(
                USER_EMAIL, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        UserModel user = UserModel.create(USER_EMAIL, "buyer1", "hash", "Buyer", null, "300", List.of());
        org.mockito.Mockito.lenient()
                .when(userService.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(user));

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
    void placeOrder_validRequest_returns201() throws Exception {
        Order order = buildOrder();
        when(orderService.placeOrder(any(), eq(IDEM_KEY), eq(ADDR_ID), any(), any()))
                .thenReturn(order);

        mockMvc.perform(post("/orders")
                        .header("Idempotency-Key", IDEM_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"shippingAddressId":"%s",
                                 "shippingSelections":{}}
                                """.formatted(ADDR_ID)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"))
                .andExpect(jsonPath("$.idempotencyKey").value(IDEM_KEY))
                .andExpect(jsonPath("$.subOrders.length()").value(1));
    }

    @Test
    void placeOrder_missingIdempotencyKeyHeader_returns400() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"shippingAddressId":"%s"}
                                """.formatted(ADDR_ID)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void placeOrder_missingShippingAddressId_returns400() throws Exception {
        mockMvc.perform(post("/orders")
                        .header("Idempotency-Key", IDEM_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void placeOrder_emptyCart_returns422() throws Exception {
        when(orderService.placeOrder(any(), any(), any(), any(), any()))
                .thenThrow(new EmptyCartException("Cart is empty."));

        mockMvc.perform(post("/orders")
                        .header("Idempotency-Key", IDEM_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"shippingAddressId":"%s"}
                                """.formatted(ADDR_ID)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void placeOrder_idempotentRetry_returns201WithSameOrder() throws Exception {
        Order existing = buildOrder();
        when(orderService.placeOrder(any(), eq(IDEM_KEY), eq(ADDR_ID), any(), any()))
                .thenReturn(existing);

        mockMvc.perform(post("/orders")
                        .header("Idempotency-Key", IDEM_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"shippingAddressId":"%s"}
                                """.formatted(ADDR_ID)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idempotencyKey").value(IDEM_KEY));
    }

    @Test
    void listOrders_returnsOrderList() throws Exception {
        when(orderService.listOrders(any())).thenReturn(List.of(buildOrder(), buildOrder()));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void listOrders_empty_returnsEmptyArray() throws Exception {
        when(orderService.listOrders(any())).thenReturn(List.of());

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getOrder_ownerUser_returns200() throws Exception {
        Order order = buildOrder();
        when(orderService.getOrder(any(), eq(order.getId().getValue())))
                .thenReturn(order);

        mockMvc.perform(get("/orders/" + order.getId().getValue()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId().getValue()));
    }

    @Test
    void getOrder_notFound_returns404() throws Exception {
        String id = OrderId.generate().getValue();
        when(orderService.getOrder(any(), eq(id)))
                .thenThrow(new OrderNotFoundException(id));

        mockMvc.perform(get("/orders/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOrder_differentUser_returns403() throws Exception {
        String id = OrderId.generate().getValue();
        when(orderService.getOrder(any(), eq(id)))
                .thenThrow(new OrderAccessDeniedException());

        mockMvc.perform(get("/orders/" + id))
                .andExpect(status().isForbidden());
    }

    @Test
    void cancelOrder_validRequest_returns200WithCancelledStatus() throws Exception {
        Order cancelled = buildOrderWithStatus(OrderStatus.CANCELLED);
        when(orderService.cancelOrder(any(), any())).thenReturn(cancelled);

        mockMvc.perform(patch("/orders/" + cancelled.getId().getValue() + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void cancelOrder_notAllowedStatus_returns409() throws Exception {
        when(orderService.cancelOrder(any(), any()))
                .thenThrow(new OrderCancellationNotAllowedException("SHIPPED"));

        mockMvc.perform(patch("/orders/" + OrderId.generate().getValue() + "/cancel"))
                .andExpect(status().isConflict());
    }

    @Test
    void cancelOrder_notFound_returns404() throws Exception {
        String id = OrderId.generate().getValue();
        when(orderService.cancelOrder(any(), eq(id)))
                .thenThrow(new OrderNotFoundException(id));

        mockMvc.perform(patch("/orders/" + id + "/cancel"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelOrder_wrongUser_returns403() throws Exception {
        when(orderService.cancelOrder(any(), any()))
                .thenThrow(new OrderAccessDeniedException());

        mockMvc.perform(patch("/orders/" + OrderId.generate().getValue() + "/cancel"))
                .andExpect(status().isForbidden());
    }

    private Order buildOrderWithStatus(OrderStatus status) {
        Cart cart = Cart.create(USER_ID);
        cart.addItem(ProductId.of("bc66d237-f54d-4bad-b521-0eba3b06304c"),
                null, null, null, SELLER_ID, 1,
                Money.of(new BigDecimal("50.00"), "USD"), "Product", "product", null);
        OrderId orderId = OrderId.generate();
        SubOrder subOrder = SubOrder.create(orderId, SELLER_ID, "Test Store",
                cart.getItems(), ZERO, null);
        return Order.reconstitute(orderId, USER_ID, "O0000000001", IDEM_KEY,
                List.of(subOrder), ZERO, ZERO, ZERO, ZERO, status, ADDR_ID, null, null, null, null);
    }

    private Order buildOrder() {
        Cart cart = Cart.create(USER_ID);
        cart.addItem(ProductId.of("bc66d237-f54d-4bad-b521-0eba3b06304c"),
                null, null, null, SELLER_ID, 1,
                Money.of(new BigDecimal("50.00"), "USD"), "Product", "product", null);

        OrderId orderId = OrderId.generate();
        SubOrder subOrder = SubOrder.create(orderId, SELLER_ID, "Test Store",
                cart.getItems(), ZERO, null);

        return Order.place(orderId, USER_ID, "O0000000001", IDEM_KEY,
                List.of(subOrder), ZERO, ADDR_ID, null, null);
    }
}
