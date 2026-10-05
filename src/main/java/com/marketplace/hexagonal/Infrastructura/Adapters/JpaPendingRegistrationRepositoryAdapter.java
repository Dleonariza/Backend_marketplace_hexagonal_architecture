package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.Auth.Models.PendingRegistration;
import com.marketplace.hexagonal.Domain.Auth.Ports.Out.PendingRegistrationRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Mappers.AuthMapper;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaPendingRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaPendingRegistrationRepositoryAdapter implements PendingRegistrationRepositoryPort {

    private final JpaPendingRegistrationRepository jpaRepository;
    private final AuthMapper authMapper;

    @Override
    public PendingRegistration save(PendingRegistration pending) {
        return authMapper.toModel(jpaRepository.save(authMapper.toEntity(pending)));
    }

    @Override
    public Optional<PendingRegistration> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(authMapper::toModel);
    }

    @Override
    public void delete(PendingRegistration pending) {
        jpaRepository.findByEmail(pending.getEmail()).ifPresent(jpaRepository::delete);
    }
}
