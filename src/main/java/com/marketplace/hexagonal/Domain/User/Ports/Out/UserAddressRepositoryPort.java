package com.marketplace.hexagonal.Domain.User.Ports.Out;

import com.marketplace.hexagonal.Domain.User.Models.UserAddress;
import com.marketplace.hexagonal.Domain.User.Models.UserAddressId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.util.List;
import java.util.Optional;

public interface UserAddressRepositoryPort {
    UserAddress save(UserAddress address, UserId userId);
    Optional<UserAddress> findById(UserAddressId id);
    List<UserAddress> findAllByUserId(UserId userId);
    void delete(UserAddressId id);
    void clearDefaultByUserId(UserId userId);
    boolean existsByIdAndUserId(UserAddressId id, UserId userId);
}
