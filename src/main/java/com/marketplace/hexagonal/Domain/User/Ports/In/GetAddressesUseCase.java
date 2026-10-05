package com.marketplace.hexagonal.Domain.User.Ports.In;

import com.marketplace.hexagonal.Domain.User.Models.UserAddress;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.util.List;

public interface GetAddressesUseCase {
    List<UserAddress> getAddresses(UserId userId);
}
