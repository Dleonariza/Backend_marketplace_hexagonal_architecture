package com.marketplace.hexagonal.Domain.User.Ports.In;

import com.marketplace.hexagonal.Domain.User.Models.UserAddress;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public interface AddAddressUseCase {
    UserAddress addAddress(UserId userId, UserAddress address);
}
