package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.Auth.Models.PasswordResetToken;
import com.marketplace.hexagonal.Domain.Auth.Ports.Out.PasswordResetTokenRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Mappers.AuthMapper;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaPasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaPasswordResetTokenRepositoryAdapter implements PasswordResetTokenRepositoryPort {

    private final JpaPasswordResetTokenRepository jpaRepository;
    private final AuthMapper authMapper;

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        return authMapper.toModel(jpaRepository.save(authMapper.toEntity(token)));
    }

    @Override
    public Optional<PasswordResetToken> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(authMapper::toModel);
    }

    @Override
    public void delete(PasswordResetToken token) {
        jpaRepository.findByEmail(token.getEmail()).ifPresent(jpaRepository::delete);
    }
}
