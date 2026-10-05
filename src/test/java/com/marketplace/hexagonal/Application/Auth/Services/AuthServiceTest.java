package com.marketplace.hexagonal.Application.Auth.Services;

import com.marketplace.hexagonal.Domain.Auth.Exceptions.ExpiredTokenException;
import com.marketplace.hexagonal.Domain.Auth.Exceptions.InvalidTokenException;
import com.marketplace.hexagonal.Domain.Auth.Models.AuthTokens;
import com.marketplace.hexagonal.Domain.Auth.Models.RefreshToken;
import com.marketplace.hexagonal.Domain.Auth.Ports.Out.RefreshTokenRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Security.Jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private UserDetailsService userDetailsService;
    @Mock private RefreshTokenRepositoryPort refreshTokenRepository;
    @Mock private UserRepositoryPort userRepositoryPort;
    @InjectMocks
    private AuthService authService;

    private final String EMAIL = "user@test.com";
    private final String PASSWORD = "password123";

    @Test
    void login_whenValidCredentials_shouldReturnTokensAndSaveRefreshToken() {
        UserDetails userDetails = buildUserDetails();
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtTokenProvider.generateAccessToken(userDetails)).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(userDetails)).thenReturn("refresh-token");
        when(jwtTokenProvider.getRefreshExpirationMs()).thenReturn(604800000L);
        when(jwtTokenProvider.getAccessExpirationMs()).thenReturn(900000L);
        when(refreshTokenRepository.save(any())).thenReturn(mock(RefreshToken.class));
        when(userRepositoryPort.findByEmail(EMAIL)).thenReturn(Optional.of(buildUserModel()));

        AuthTokens result = authService.login(EMAIL, PASSWORD);

        assertThat(result.getAccessToken()).isEqualTo("access-token");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getRoles()).contains("ROLE_BUYER");
        verify(refreshTokenRepository).deleteAllByUserEmail(EMAIL);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void login_whenBadCredentials_shouldThrowBadCredentialsException() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(EMAIL, "wrong-password"))
                .isInstanceOf(BadCredentialsException.class);
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void refresh_whenValidToken_shouldRotateRefreshToken() {
        String rawToken = "raw-refresh-token";
        RefreshToken stored = RefreshToken.builder()
                .tokenHash("some-hash")
                .userEmail(EMAIL)
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        UserDetails userDetails = buildUserDetails();

        when(jwtTokenProvider.validateToken(rawToken)).thenReturn(true);
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(stored));
        when(jwtTokenProvider.extractUsername(rawToken)).thenReturn(EMAIL);
        when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(userDetails);
        when(jwtTokenProvider.generateAccessToken(userDetails)).thenReturn("new-access");
        when(jwtTokenProvider.generateRefreshToken(userDetails)).thenReturn("new-refresh");
        when(jwtTokenProvider.getRefreshExpirationMs()).thenReturn(604800000L);
        when(jwtTokenProvider.getAccessExpirationMs()).thenReturn(900000L);
        when(refreshTokenRepository.save(any())).thenReturn(mock(RefreshToken.class));

        AuthTokens result = authService.refresh(rawToken);

        assertThat(result.getAccessToken()).isEqualTo("new-access");
        assertThat(result.getRefreshToken()).isEqualTo("new-refresh");
        verify(refreshTokenRepository).deleteByTokenHash(any());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void refresh_whenTokenSignatureInvalid_throwsInvalidTokenException() {
        when(jwtTokenProvider.validateToken(any())).thenReturn(false);

        assertThatThrownBy(() -> authService.refresh("invalid-token"))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void refresh_whenTokenExpiredInDb_shouldThrowExpiredTokenException() {
        RefreshToken expired = RefreshToken.builder()
                .tokenHash("hash")
                .userEmail(EMAIL)
                .expiresAt(Instant.now().minusSeconds(3600))
                .build();

        when(jwtTokenProvider.validateToken(any())).thenReturn(true);
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> authService.refresh("expired-token"))
                .isInstanceOf(ExpiredTokenException.class);
        verify(refreshTokenRepository).deleteByTokenHash(any());
    }

    @Test
    void refresh_whenTokenNotInDb_shouldThrowInvalidTokenException() {
        when(jwtTokenProvider.validateToken(any())).thenReturn(true);
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh("unknown-token"))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void logout_whenTokenExists_shouldDeleteIt() {
        RefreshToken token = RefreshToken.builder().tokenHash("stored-hash").build();
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(token));

        authService.logout("raw-token");

        verify(refreshTokenRepository).deleteByTokenHash("stored-hash");
    }

    @Test
    void logout_whenTokenNotFound_shouldDoNothing() {
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.empty());

        authService.logout("not-found-token");

        verify(refreshTokenRepository, never()).deleteByTokenHash(any());
    }

    private UserDetails buildUserDetails() {
        return User.builder()
                .username(EMAIL)
                .password(PASSWORD)
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_BUYER")))
                .build();
    }

    private UserModel buildUserModel() {
        return UserModel.builder()
                .userId(UserId.of("550e8400-e29b-41d4-a716-446655440000"))
                .email(EMAIL)
                .name("Test User")
                .userName("testuser")
                .avatarUrl(null)
                .build();
    }
}
