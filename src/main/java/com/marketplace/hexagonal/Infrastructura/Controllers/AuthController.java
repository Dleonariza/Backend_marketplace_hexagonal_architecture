package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Auth.Services.AuthService;
import com.marketplace.hexagonal.Application.Auth.Services.PasswordResetService;
import com.marketplace.hexagonal.Domain.Auth.Exceptions.InvalidTokenException;
import com.marketplace.hexagonal.Domain.Auth.Models.AuthTokens;
import com.marketplace.hexagonal.Infrastructura.DTO.Auth.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String CLIENT_TYPE_HEADER = "X-Client-Type";
    private static final String WEB_CLIENT = "web";
    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request,
            @RequestHeader(value = CLIENT_TYPE_HEADER, defaultValue = WEB_CLIENT) String clientType,
            HttpServletResponse response) {
        AuthTokens tokens = authService.login(request.email(), request.password());

        if (isWeb(clientType)) {
            setRefreshCookie(response, tokens.getRefreshToken());
            return ResponseEntity.ok(LoginResponseDTO.ofWeb(
                    tokens.getAccessToken(), tokens.getExpiresIn(),
                    tokens.getEmail(), tokens.getRoles(),
                    tokens.getUserId(), tokens.getName(),
                    tokens.getUserName(), tokens.getAvatarUrl()));
        }

        return ResponseEntity.ok(LoginResponseDTO.of(
                tokens.getAccessToken(), tokens.getRefreshToken(),
                tokens.getExpiresIn(), tokens.getEmail(), tokens.getRoles(),
                tokens.getUserId(), tokens.getName(),
                tokens.getUserName(), tokens.getAvatarUrl()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponseDTO> refresh(
            @RequestHeader(value = CLIENT_TYPE_HEADER, defaultValue = WEB_CLIENT) String clientType,
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String cookieRefreshToken,
            @RequestBody(required = false) TokenRefreshRequestDTO body,
            HttpServletResponse response) {
        String rawToken = isWeb(clientType)
                ? cookieRefreshToken
                : (body != null ? body.refreshToken() : null);

        if (rawToken == null || rawToken.isBlank()) {
            throw new InvalidTokenException("Refresh token is missing.");
        }

        AuthTokens tokens = authService.refresh(rawToken);

        if (isWeb(clientType)) {
            setRefreshCookie(response, tokens.getRefreshToken());
            return ResponseEntity.ok(TokenRefreshResponseDTO.ofWeb(
                    tokens.getAccessToken(), tokens.getExpiresIn()));
        }

        return ResponseEntity.ok(TokenRefreshResponseDTO.of(
                tokens.getAccessToken(), tokens.getRefreshToken(),
                tokens.getExpiresIn()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = CLIENT_TYPE_HEADER, defaultValue = WEB_CLIENT) String clientType,
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String cookieRefreshToken,
            @RequestBody(required = false) TokenRefreshRequestDTO body,
            HttpServletResponse response) {
        String rawToken = isWeb(clientType)
                ? cookieRefreshToken
                : (body != null ? body.refreshToken() : null);

        if (rawToken != null && !rawToken.isBlank()) {
            authService.logout(rawToken);
        }

        if (isWeb(clientType)) {
            clearRefreshCookie(response);
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        passwordResetService.forgotPassword(request.email());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        passwordResetService.resetPassword(request.email(), request.code(), request.newPassword());
        return ResponseEntity.noContent().build();
    }

    private boolean isWeb(String clientType) {
        return WEB_CLIENT.equalsIgnoreCase(clientType);
    }

    private void setRefreshCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/auth")
                .maxAge(Duration.ofMillis(refreshExpirationMs))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
