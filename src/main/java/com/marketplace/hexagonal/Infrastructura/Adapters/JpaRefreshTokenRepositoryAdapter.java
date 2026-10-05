package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.Auth.Models.RefreshToken;
import com.marketplace.hexagonal.Domain.Auth.Ports.Out.RefreshTokenRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Mappers.AuthMapper;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaRefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final JpaRefreshTokenRepository jpaRepository;
    private final AuthMapper authMapper;

    @Override
    public RefreshToken save(RefreshToken token) {
        return authMapper.toModel(jpaRepository.save(authMapper.toEntity(token)));
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(authMapper::toModel);
    }

    @Override
    public void deleteByTokenHash(String tokenHash) {
        jpaRepository.deleteByTokenHash(tokenHash);
    }

    @Override
    public void deleteAllByUserEmail(String userEmail) {
        jpaRepository.deleteAllByUserEmail(userEmail);
    }
}
