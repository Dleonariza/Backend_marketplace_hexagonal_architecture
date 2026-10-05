package com.marketplace.hexagonal.Domain.User.Ports.In;

import com.marketplace.hexagonal.Domain.User.Models.UserAddress;
import com.marketplace.hexagonal.Domain.User.Models.UserAddressId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public interface UpdateAddressUseCase {
    UserAddress updateAddress(UserId userId, UserAddressId addressId, UserAddress updated);
}
