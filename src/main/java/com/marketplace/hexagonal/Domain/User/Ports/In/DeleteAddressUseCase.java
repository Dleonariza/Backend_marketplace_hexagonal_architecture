package com.marketplace.hexagonal.Domain.User.Ports.In;

import com.marketplace.hexagonal.Domain.User.Models.UserAddressId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public interface DeleteAddressUseCase {
    void deleteAddress(UserId userId, UserAddressId addressId);
}
