package com.marketplace.hexagonal.Infrastructura.Controllers;

import tools.jackson.databind.ObjectMapper;
import com.marketplace.hexagonal.Application.User.Services.PaymentMethodService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.User.Exceptions.PaymentMethodNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.PaymentMethod;
import com.marketplace.hexagonal.Domain.User.Models.PaymentMethodId;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Infrastructura.DTO.PaymentMethod.AddPaymentMethodRequestDTO;
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

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentMethodControllerTest {

    @Mock PaymentMethodService paymentMethodService;
    @Mock UserService userService;
    @InjectMocks PaymentMethodController controller;
    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    private static final String USER_EMAIL = "buyer@test.com";
    private static final String PM_ID = "660e8400-e29b-41d4-a716-446655440001";

    @BeforeEach
    void setUp() {
        var auth = new UsernamePasswordAuthenticationToken(
                USER_EMAIL, null,
                List.of(new SimpleGrantedAuthority("ROLE_BUYER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        UserModel user = UserModel.create(
                USER_EMAIL, "buyer1", "hash", "Buyer", null, "3001234567", List.of());
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
    void listPaymentMethods_shouldReturn200WithCards() throws Exception {
        when(paymentMethodService.listPaymentMethods(any()))
                .thenReturn(List.of(buildCard("4242", true), buildCard("5555", false)));

        mockMvc.perform(get("/user/payment-methods"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].last4").value("4242"))
                .andExpect(jsonPath("$[0].isDefault").value(true))
                .andExpect(jsonPath("$[1].last4").value("5555"))
                .andExpect(jsonPath("$[1].isDefault").value(false));
    }

    @Test
    void listPaymentMethods_whenNoCards_shouldReturn200WithEmptyArray() throws Exception {
        when(paymentMethodService.listPaymentMethods(any())).thenReturn(List.of());

        mockMvc.perform(get("/user/payment-methods"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void addPaymentMethod_whenValidRequest_shouldReturn201() throws Exception {
        PaymentMethod saved = buildCard("4242", true);
        when(paymentMethodService.addPaymentMethod(any(), any())).thenReturn(saved);

        mockMvc.perform(post("/user/payment-methods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.last4").value("4242"))
                .andExpect(jsonPath("$.brand").value("visa"))
                .andExpect(jsonPath("$.expMonth").value(12))
                .andExpect(jsonPath("$.expYear").value(2028))
                .andExpect(jsonPath("$.cardholderName").value("John Doe"))
                .andExpect(jsonPath("$.isDefault").value(true));
    }

    @Test
    void addPaymentMethod_whenLast4Missing_shouldReturn400() throws Exception {
        AddPaymentMethodRequestDTO request = new AddPaymentMethodRequestDTO(
                null, null, "visa", 12, 2028, "John Doe", false);

        mockMvc.perform(post("/user/payment-methods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addPaymentMethod_whenLast4IsNotFourDigits_shouldReturn400() throws Exception {
        AddPaymentMethodRequestDTO request = new AddPaymentMethodRequestDTO(
                null, "123", "visa", 12, 2028, "John Doe", false);

        mockMvc.perform(post("/user/payment-methods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addPaymentMethod_whenLast4ContainsLetters_shouldReturn400() throws Exception {
        AddPaymentMethodRequestDTO request = new AddPaymentMethodRequestDTO(
                null, "42AB", "visa", 12, 2028, "John Doe", false);

        mockMvc.perform(post("/user/payment-methods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addPaymentMethod_whenExpMonthIsZero_shouldReturn400() throws Exception {
        AddPaymentMethodRequestDTO request = new AddPaymentMethodRequestDTO(
                null, "4242", "visa", 0, 2028, "John Doe", false);

        mockMvc.perform(post("/user/payment-methods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addPaymentMethod_whenExpMonthIsThirteen_shouldReturn400() throws Exception {
        AddPaymentMethodRequestDTO request = new AddPaymentMethodRequestDTO(
                null, "4242", "visa", 13, 2028, "John Doe", false);

        mockMvc.perform(post("/user/payment-methods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addPaymentMethod_whenCardholderNameMissing_shouldReturn400() throws Exception {
        AddPaymentMethodRequestDTO request = new AddPaymentMethodRequestDTO(
                null, "4242", "visa", 12, 2028, "", false);

        mockMvc.perform(post("/user/payment-methods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deletePaymentMethod_whenExists_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/user/payment-methods/" + PM_ID))
                .andExpect(status().isNoContent());

        verify(paymentMethodService).deletePaymentMethod(any(), any());
    }

    @Test
    void deletePaymentMethod_whenNotFound_shouldReturn404() throws Exception {
        doThrow(new PaymentMethodNotFoundException(PaymentMethodId.of(PM_ID)))
                .when(paymentMethodService).deletePaymentMethod(any(), any());

        mockMvc.perform(delete("/user/payment-methods/" + PM_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void setDefault_whenExists_shouldReturn200WithIsDefaultTrue() throws Exception {
        PaymentMethod updated = buildCard("4242", true);
        when(paymentMethodService.setDefaultPaymentMethod(any(), any())).thenReturn(updated);

        mockMvc.perform(patch("/user/payment-methods/" + PM_ID + "/default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isDefault").value(true));
    }

    @Test
    void setDefault_whenNotFound_shouldReturn404() throws Exception {
        when(paymentMethodService.setDefaultPaymentMethod(any(), any()))
                .thenThrow(new PaymentMethodNotFoundException(PaymentMethodId.of(PM_ID)));

        mockMvc.perform(patch("/user/payment-methods/" + PM_ID + "/default"))
                .andExpect(status().isNotFound());
    }

    private PaymentMethod buildCard(String last4, boolean isDefault) {
        return PaymentMethod.builder()
                .id(PaymentMethodId.of(PM_ID))
                .userId(com.marketplace.hexagonal.Domain.User.Models.UserId
                        .of("550e8400-e29b-41d4-a716-446655440000"))
                .stripePaymentMethodId("pm_test_123")
                .last4(last4)
                .brand("visa")
                .expMonth(12)
                .expYear(2028)
                .cardholderName("John Doe")
                .isDefault(isDefault)
                .createdAt(Instant.now())
                .build();
    }

    private AddPaymentMethodRequestDTO buildValidRequest() {
        return new AddPaymentMethodRequestDTO(
                "pm_test_123", "4242", "visa", 12, 2028, "John Doe", false);
    }
}
