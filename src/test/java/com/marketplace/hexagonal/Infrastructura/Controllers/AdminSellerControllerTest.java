package com.marketplace.hexagonal.Infrastructura.Controllers;

import tools.jackson.databind.ObjectMapper;
import com.marketplace.hexagonal.Application.Seller.Services.SellerService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Seller.Exceptions.InvalidSellerStatusTransitionException;
import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerProfileNotFoundException;
import com.marketplace.hexagonal.Domain.Seller.Models.*;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.DTO.Seller.SellerReviewRequestDTO;
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
class AdminSellerControllerTest {

    @Mock SellerService sellerService;
    @Mock UserService userService;
    @InjectMocks AdminSellerController adminSellerController;
    MockMvc mockMvc;
    final ObjectMapper objectMapper = new ObjectMapper();

    private static final String ADMIN_EMAIL = "admin@test.com";
    private static final String ADMIN_ID = "660e8400-e29b-41d4-a716-446655440001";
    private static final String PROFILE_ID = "770e8400-e29b-41d4-a716-446655440002";

    @BeforeEach
    void setUp() {
        var auth = new UsernamePasswordAuthenticationToken(
                ADMIN_EMAIL, null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        UserModel admin = UserModel.create(ADMIN_EMAIL, "admin", "hash", "Admin", null, "3000000000", List.of());
        org.mockito.Mockito.lenient()
                .when(userService.findByEmail(ADMIN_EMAIL))
                .thenReturn(Optional.of(admin));

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(adminSellerController)
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
    void getPending_shouldReturn200WithList() throws Exception {
        when(sellerService.getPendingApplications())
                .thenReturn(List.of(buildProfile(SellerStatus.PENDING_APPROVAL)));

        mockMvc.perform(get("/admin/sellers/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING_APPROVAL"));
    }

    @Test
    void getAllSellers_shouldReturn200WithList() throws Exception {
        when(sellerService.getAllSellers())
                .thenReturn(List.of(
                        buildProfile(SellerStatus.ACTIVE),
                        buildProfile(SellerStatus.PENDING_APPROVAL)));

        mockMvc.perform(get("/admin/sellers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getById_whenExists_shouldReturn200() throws Exception {
        when(sellerService.getById(any())).thenReturn(buildProfile(SellerStatus.PENDING_APPROVAL));

        mockMvc.perform(get("/admin/sellers/" + PROFILE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PROFILE_ID));
    }

    @Test
    void getById_whenNotFound_shouldReturn404() throws Exception {
        when(sellerService.getById(any()))
                .thenThrow(new SellerProfileNotFoundException(SellerProfileId.of(PROFILE_ID)));

        mockMvc.perform(get("/admin/sellers/" + PROFILE_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void approve_whenValid_shouldReturn200WithActiveStatus() throws Exception {
        when(sellerService.approve(any(), any())).thenReturn(buildProfile(SellerStatus.ACTIVE));

        mockMvc.perform(post("/admin/sellers/" + PROFILE_ID + "/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void approve_whenInvalidTransition_shouldReturn409() throws Exception {
        when(sellerService.approve(any(), any()))
                .thenThrow(new InvalidSellerStatusTransitionException(
                        SellerStatus.ACTIVE, SellerStatus.ACTIVE));

        mockMvc.perform(post("/admin/sellers/" + PROFILE_ID + "/approve"))
                .andExpect(status().isConflict());
    }

    @Test
    void reject_whenValid_shouldReturn200WithRejectedStatus() throws Exception {
        when(sellerService.reject(any(), any(), any())).thenReturn(buildProfile(SellerStatus.REJECTED));

        mockMvc.perform(post("/admin/sellers/" + PROFILE_ID + "/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SellerReviewRequestDTO("Datos incorrectos"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void reject_whenMissingReason_shouldReturn400() throws Exception {
        mockMvc.perform(post("/admin/sellers/" + PROFILE_ID + "/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void suspend_whenValid_shouldReturn200WithSuspendedStatus() throws Exception {
        when(sellerService.suspend(any(), any(), any())).thenReturn(buildProfile(SellerStatus.SUSPENDED));

        mockMvc.perform(post("/admin/sellers/" + PROFILE_ID + "/suspend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SellerReviewRequestDTO("Fraude detectado"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUSPENDED"));
    }

    @Test
    void reactivate_whenValid_shouldReturn200WithActiveStatus() throws Exception {
        when(sellerService.reactivate(any(), any())).thenReturn(buildProfile(SellerStatus.ACTIVE));

        mockMvc.perform(post("/admin/sellers/" + PROFILE_ID + "/reactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    private SellerProfile buildProfile(SellerStatus status) {
        return SellerProfile.reconstitute(
                SellerProfileId.of(PROFILE_ID),
                UserId.of("550e8400-e29b-41d4-a716-446655440000"),
                "Mi Tienda", "Tienda de tecnología", "Electronics",
                "contacto@mitienda.com", "3001234567", null,
                new IdentityDocument(DocumentType.CC, "12345678"),
                new BankingInfo("Bancolombia", "12345678901", AccountType.SAVINGS, "John Doe", "12345678"),
                status,
                status == SellerStatus.REJECTED ? "Datos incorrectos" : null,
                status == SellerStatus.SUSPENDED ? "Fraude detectado" : null,
                Instant.now(), null, null
        );
    }
}
