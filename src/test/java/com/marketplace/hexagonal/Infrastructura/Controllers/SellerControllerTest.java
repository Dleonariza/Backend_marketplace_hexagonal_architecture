package com.marketplace.hexagonal.Infrastructura.Controllers;

import tools.jackson.databind.ObjectMapper;
import com.marketplace.hexagonal.Application.Seller.Services.SellerService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerAlreadyAppliedException;
import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerProfileNotFoundException;
import com.marketplace.hexagonal.Domain.Seller.Models.*;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.DTO.Seller.SellerApplicationRequestDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Seller.UpdateSellerProfileRequestDTO;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SellerControllerTest {

    @Mock SellerService sellerService;
    @Mock UserService userService;
    @InjectMocks SellerController sellerController;
    MockMvc mockMvc;
    final ObjectMapper objectMapper = new ObjectMapper();

    private static final String USER_EMAIL = "buyer@test.com";
    private static final String USER_ID_STR = "550e8400-e29b-41d4-a716-446655440000";
    private static final String PROFILE_ID = "770e8400-e29b-41d4-a716-446655440002";

    @BeforeEach
    void setUp() {
        var auth = new UsernamePasswordAuthenticationToken(
                USER_EMAIL, null,
                List.of(new SimpleGrantedAuthority("ROLE_BUYER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        UserModel user = UserModel.create(USER_EMAIL, "buyer1", "hash", "Buyer", null, "3001234567", List.of());
        org.mockito.Mockito.lenient()
                .when(userService.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(user));

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(sellerController)
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
    void apply_whenValidRequest_shouldReturn201() throws Exception {
        when(sellerService.apply(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(buildProfile(SellerStatus.PENDING_APPROVAL));

        mockMvc.perform(post("/seller/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING_APPROVAL"))
                .andExpect(jsonPath("$.storeName").value("Mi Tienda"));
    }

    @Test
    void apply_whenMissingRequiredFields_shouldReturn400() throws Exception {
        SellerApplicationRequestDTO bad = new SellerApplicationRequestDTO(
                "", "desc", "cat", "e@e.com", "3001234567", null,
                DocumentType.CC, "12345678",
                "Banco", "123456", AccountType.SAVINGS, "Holder", "12345678");

        mockMvc.perform(post("/seller/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.storeName").exists());
    }

    @Test
    void apply_whenAlreadyApplied_shouldReturn409() throws Exception {
        when(sellerService.apply(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new SellerAlreadyAppliedException());

        mockMvc.perform(post("/seller/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isConflict());
    }

    @Test
    void getMyProfile_whenExists_shouldReturn200() throws Exception {
        when(sellerService.findByUserId(any())).thenReturn(Optional.of(buildProfile(SellerStatus.ACTIVE)));

        mockMvc.perform(get("/seller/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.storeName").value("Mi Tienda"));
    }

    @Test
    void getMyProfile_whenNotFound_shouldReturn404() throws Exception {
        when(sellerService.findByUserId(any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/seller/me"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getDashboard_whenActive_shouldReturnDashboardWithMaskedData() throws Exception {
        when(sellerService.findByUserId(any())).thenReturn(Optional.of(buildProfile(SellerStatus.ACTIVE)));

        mockMvc.perform(get("/seller/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.store.storeName").value("Mi Tienda"))
                .andExpect(jsonPath("$.status.code").value("ACTIVE"))
                .andExpect(jsonPath("$.banking.maskedAccountNumber").value("****8901"))
                .andExpect(jsonPath("$.document.maskedDocumentNumber").value("****5678"))
                .andExpect(jsonPath("$.stats.products").value(0));
    }

    @Test
    void getDashboard_whenNoProfile_shouldReturn404() throws Exception {
        when(sellerService.findByUserId(any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/seller/dashboard"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProfile_whenValidRequest_shouldReturn200() throws Exception {
        UpdateSellerProfileRequestDTO req = new UpdateSellerProfileRequestDTO(
                "Nueva Tienda", "Nueva descripcion", "Electronics",
                "nuevo@tienda.com", "3009999999", null);

        when(sellerService.updateProfile(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(buildProfile(SellerStatus.ACTIVE));

        mockMvc.perform(put("/seller/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeName").value("Mi Tienda"));
    }

    @Test
    void updateProfile_whenMissingFields_shouldReturn400() throws Exception {
        UpdateSellerProfileRequestDTO bad = new UpdateSellerProfileRequestDTO(
                "", "desc", "cat", "bad-email", "3001234567", null);

        mockMvc.perform(put("/seller/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.storeName").exists());
    }

    private SellerApplicationRequestDTO buildRequest() {
        return new SellerApplicationRequestDTO(
                "Mi Tienda", "Tienda de tecnología", "Electronics",
                "contacto@mitienda.com", "3001234567", null,
                DocumentType.CC, "12345678",
                "Bancolombia", "12345678901", AccountType.SAVINGS,
                "John Doe", "12345678");
    }

    private SellerProfile buildProfile(SellerStatus status) {
        return SellerProfile.reconstitute(
                SellerProfileId.of(PROFILE_ID),
                UserId.of(USER_ID_STR),
                "Mi Tienda", "Tienda de tecnología", "Electronics",
                "contacto@mitienda.com", "3001234567", null,
                new IdentityDocument(DocumentType.CC, "12345678"),
                new BankingInfo("Bancolombia", "12345678901", AccountType.SAVINGS, "John Doe", "12345678"),
                status, null, null,
                Instant.now(), null, null
        );
    }
}
