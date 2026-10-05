package com.marketplace.hexagonal.Application.Auth.Services;

import com.marketplace.hexagonal.Domain.Auth.Exceptions.ExpiredTokenException;
import com.marketplace.hexagonal.Domain.Auth.Exceptions.InvalidTokenException;
import com.marketplace.hexagonal.Domain.Auth.Models.AuthTokens;
import com.marketplace.hexagonal.Domain.Auth.Models.RefreshToken;
import com.marketplace.hexagonal.Domain.Auth.Ports.In.LoginUseCase;
import com.marketplace.hexagonal.Domain.Auth.Ports.In.LogoutUseCase;
import com.marketplace.hexagonal.Domain.Auth.Ports.In.RefreshTokenUseCase;
import com.marketplace.hexagonal.Domain.Auth.Ports.Out.RefreshTokenRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Security.Jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService implements LoginUseCase, RefreshTokenUseCase, LogoutUseCase {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final UserRepositoryPort userRepositoryPort;

    @Override
    @Transactional
    public AuthTokens login(String email, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String rawRefresh = jwtTokenProvider.generateRefreshToken(userDetails);

        refreshTokenRepository.deleteAllByUserEmail(userDetails.getUsername());
        refreshTokenRepository.save(RefreshToken.builder()
                .tokenHash(hash(rawRefresh))
                .userEmail(userDetails.getUsername())
                .expiresAt(Instant.now().plusMillis(jwtTokenProvider.getRefreshExpirationMs()))
                .build());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).filter(Objects::nonNull)
                .filter(a -> a.startsWith("ROLE_"))
                .toList();

        UserModel profile = userRepositoryPort.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in repository"));

        return AuthTokens.builder()
                .accessToken(accessToken)
                .refreshToken(rawRefresh)
                .expiresIn(jwtTokenProvider.getAccessExpirationMs())
                .email(userDetails.getUsername())
                .roles(roles)
                .userId(profile.getUserId().getValue().toString())
                .name(profile.getName())
                .userName(profile.getUserName())
                .avatarUrl(profile.getAvatarUrl())
                .build();
    }

    @Override
    @Transactional
    public AuthTokens refresh(String rawRefreshToken) {
        if (!jwtTokenProvider.validateToken(rawRefreshToken)) {
            throw new InvalidTokenException("Refresh token is invalid or expired.");
        }

        String tokenHash = hash(rawRefreshToken);
        RefreshToken stored = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not recognized."));

        if (stored.isExpired()) {
            refreshTokenRepository.deleteByTokenHash(tokenHash);
            throw new ExpiredTokenException("Refresh token has expired. Please log in again.");
        }

        String email = jwtTokenProvider.extractUsername(rawRefreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String newRawRefresh = jwtTokenProvider.generateRefreshToken(userDetails);

        refreshTokenRepository.deleteByTokenHash(tokenHash);
        refreshTokenRepository.save(RefreshToken.builder()
                .tokenHash(hash(newRawRefresh))
                .userEmail(email)
                .expiresAt(Instant.now().plusMillis(jwtTokenProvider.getRefreshExpirationMs()))
                .build());

        return AuthTokens.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRawRefresh)
                .expiresIn(jwtTokenProvider.getAccessExpirationMs())
                .build();
    }

    @Override
    @Transactional
    public void logout(String rawRefreshToken) {
        refreshTokenRepository.findByTokenHash(hash(rawRefreshToken))
                .ifPresent(t -> refreshTokenRepository.deleteByTokenHash(t.getTokenHash()));
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(encoded);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
