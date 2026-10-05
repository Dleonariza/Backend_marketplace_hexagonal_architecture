package com.marketplace.hexagonal.Infrastructura.Controllers;

import tools.jackson.databind.ObjectMapper;
import com.marketplace.hexagonal.Application.Auth.Services.AuthService;
import com.marketplace.hexagonal.Application.Auth.Services.PasswordResetService;
import com.marketplace.hexagonal.Domain.Auth.Exceptions.InvalidTokenException;
import com.marketplace.hexagonal.Domain.Auth.Models.AuthTokens;
import com.marketplace.hexagonal.Infrastructura.DTO.Auth.LoginRequestDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Auth.TokenRefreshRequestDTO;
import com.marketplace.hexagonal.Infrastructura.Exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock AuthService authService;
    @Mock PasswordResetService passwordResetService;
    @InjectMocks AuthController authController;
    MockMvc mockMvc;
    final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authController, "refreshExpirationMs", 604800000L);

        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void login_whenValidCredentials_shouldReturn200WithAccessToken() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("user@test.com", "password");
        when(authService.login("user@test.com", "password")).thenReturn(buildAuthTokens());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-Type", "web")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").doesNotExist())
                .andExpect(cookie().exists("refresh_token"));
    }

    @Test
    void login_whenMobileClient_shouldReturn200WithBothTokensInBody() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("user@test.com", "password");
        when(authService.login("user@test.com", "password")).thenReturn(buildAuthTokens());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-Type", "mobile")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void login_whenBadCredentials_shouldReturn401() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("user@test.com", "wrong-pass");
        when(authService.login(anyString(), anyString()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_whenMissingFields_shouldReturn400() throws Exception {
        String invalidBody = "{\"email\":\"user@test.com\"}";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refresh_whenWebClientWithCookie_shouldReturn200AndSetNewCookie() throws Exception {
        when(authService.refresh("stored-refresh-token")).thenReturn(buildAuthTokens());

        mockMvc.perform(post("/auth/refresh")
                        .header("X-Client-Type", "web")
                        .cookie(new jakarta.servlet.http.Cookie("refresh_token", "stored-refresh-token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").doesNotExist())
                .andExpect(cookie().exists("refresh_token"));
    }

    @Test
    void refresh_whenMobileClientWithBody_shouldReturn200WithBothTokens() throws Exception {
        TokenRefreshRequestDTO request = new TokenRefreshRequestDTO("mobile-refresh-token");
        when(authService.refresh("mobile-refresh-token")).thenReturn(buildAuthTokens());

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-Type", "mobile")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void refresh_whenTokenMissing_shouldReturn401() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .header("X-Client-Type", "web"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refresh_whenTokenInvalid_shouldReturn401() throws Exception {
        TokenRefreshRequestDTO request = new TokenRefreshRequestDTO("bad-token");
        when(authService.refresh("bad-token")).thenThrow(new InvalidTokenException("Invalid token."));

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-Type", "mobile")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_whenWebClientWithCookie_shouldReturn204AndClearCookie() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .header("X-Client-Type", "web")
                        .cookie(new jakarta.servlet.http.Cookie("refresh_token", "some-token")))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge("refresh_token", 0));
    }

    @Test
    void logout_whenMobileClientWithBody_shouldReturn204() throws Exception {
        TokenRefreshRequestDTO request = new TokenRefreshRequestDTO("mobile-refresh-token");

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-Type", "mobile")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void forgotPassword_shouldAlwaysReturn204() throws Exception {
        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"any@test.com\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void resetPassword_whenValidRequest_shouldReturn204() throws Exception {
        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@test.com\",\"code\":\"123456\",\"newPassword\":\"NewPass123!\"}"))
                .andExpect(status().isNoContent());
    }

    private AuthTokens buildAuthTokens() {
        return AuthTokens.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .expiresIn(900000L)
                .email("user@test.com")
                .roles(List.of("ROLE_BUYER"))
                .userId("550e8400-e29b-41d4-a716-446655440000")
                .name("Test User")
                .userName("testuser")
                .avatarUrl(null)
                .build();
    }
}
