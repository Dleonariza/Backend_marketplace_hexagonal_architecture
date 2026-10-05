package com.marketplace.hexagonal.Application.User.UseCases;

import com.marketplace.hexagonal.Domain.User.Models.UserAddress;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.In.GetAddressesUseCase;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserAddressRepositoryPort;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class GetAddressesUseCaseImpl implements GetAddressesUseCase {

    private final UserAddressRepositoryPort userAddressRepositoryPort;

    @Override
    public List<UserAddress> getAddresses(UserId userId) {
        return userAddressRepositoryPort.findAllByUserId(userId);
    }
}
