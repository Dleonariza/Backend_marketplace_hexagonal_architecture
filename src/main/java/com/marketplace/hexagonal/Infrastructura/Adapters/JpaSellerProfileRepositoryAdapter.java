package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerStatus;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Exceptions.UserNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.Entities.Seller.SellerProfileEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.User.UserEntity;
import com.marketplace.hexagonal.Infrastructura.Mappers.SellerProfileMapper;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaSellerProfileRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class JpaSellerProfileRepositoryAdapter implements SellerProfileRepositoryPort {

    private final JpaSellerProfileRepository jpaSellerProfileRepository;
    private final JpaUserRepository jpaUserRepository;
    private final SellerProfileMapper sellerProfileMapper;

    @Override
    public SellerProfile save(SellerProfile profile) {
        UserEntity userEntity = jpaUserRepository
                .findById(UUID.fromString(profile.getUserId().getValue()))
                .orElseThrow(() -> new UserNotFoundException(profile.getUserId()));

        SellerProfileEntity entity = sellerProfileMapper.toEntity(profile, userEntity);
        SellerProfileEntity saved = jpaSellerProfileRepository.save(entity);
        return sellerProfileMapper.toDomain(saved);
    }

    @Override
    public Optional<SellerProfile> findById(SellerProfileId id) {
        return jpaSellerProfileRepository.findById(UUID.fromString(id.getValue()))
                .map(sellerProfileMapper::toDomain);
    }

    @Override
    public Optional<SellerProfile> findByUserId(UserId userId) {
        return jpaSellerProfileRepository.findByUser_Id(UUID.fromString(userId.getValue()))
                .map(sellerProfileMapper::toDomain);
    }

    @Override
    public List<SellerProfile> findByStatus(SellerStatus status) {
        return jpaSellerProfileRepository.findByStatus(status).stream()
                .map(sellerProfileMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SellerProfile> findAll() {
        return jpaSellerProfileRepository.findAll().stream()
                .map(sellerProfileMapper::toDomain)
                .collect(Collectors.toList());
    }
}
