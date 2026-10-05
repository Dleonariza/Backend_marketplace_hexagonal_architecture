package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.User.Exceptions.AddressNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.UserAddress;
import com.marketplace.hexagonal.Domain.User.Models.UserAddressId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserAddressRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Entities.User.UserAddressEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.User.UserEntity;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaUserAddressRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class JpaUserAddressRepositoryAdapter implements UserAddressRepositoryPort {

    private final JpaUserAddressRepository jpaUserAddressRepository;
    private final JpaUserRepository jpaUserRepository;

    @Override
    @Transactional
    public UserAddress save(UserAddress address, UserId userId) {
        UserEntity userEntity = jpaUserRepository.findById(UUID.fromString(userId.getValue()))
                .orElseThrow(() -> new IllegalStateException("User not found: " + userId.getValue()));

        UUID addressId = address.getId() != null
                ? UUID.fromString(address.getId().getValue())
                : null;

        UserAddressEntity entity = (addressId != null)
                ? jpaUserAddressRepository.findById(addressId)
                        .orElse(new UserAddressEntity())
                : new UserAddressEntity();

        entity.setStreet(address.getStreet());
        entity.setCity(address.getCity());
        entity.setState(address.getState());
        entity.setPostalCode(address.getPostalCode());
        entity.setCountry(address.getCountry());
        entity.setIsDefault(address.getIsDefault());
        entity.setUser(userEntity);

        return toDomain(jpaUserAddressRepository.save(entity));
    }

    @Override
    public Optional<UserAddress> findById(UserAddressId id) {
        return jpaUserAddressRepository.findById(UUID.fromString(id.getValue()))
                .map(this::toDomain);
    }

    @Override
    public List<UserAddress> findAllByUserId(UserId userId) {
        return jpaUserAddressRepository.findAllByUserId(UUID.fromString(userId.getValue()))
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(UserAddressId id) {
        UUID uuid = UUID.fromString(id.getValue());
        if (!jpaUserAddressRepository.existsById(uuid)) {
            throw new AddressNotFoundException(id);
        }

        jpaUserAddressRepository.deleteAddressById(uuid);
    }

    @Override
    @Transactional
    public void clearDefaultByUserId(UserId userId) {
        jpaUserAddressRepository.clearDefaultByUserId(UUID.fromString(userId.getValue()));
    }

    @Override
    public boolean existsByIdAndUserId(UserAddressId id, UserId userId) {
        return jpaUserAddressRepository.existsByIdAndUserId(
                UUID.fromString(id.getValue()),
                UUID.fromString(userId.getValue()));
    }

    private UserAddress toDomain(UserAddressEntity entity) {
        return UserAddress.builder()
                .id(UserAddressId.of(entity.getId().toString()))
                .street(entity.getStreet())
                .city(entity.getCity())
                .state(entity.getState())
                .postalCode(entity.getPostalCode())
                .country(entity.getCountry())
                .isDefault(entity.getIsDefault())
                .build();
    }
}
