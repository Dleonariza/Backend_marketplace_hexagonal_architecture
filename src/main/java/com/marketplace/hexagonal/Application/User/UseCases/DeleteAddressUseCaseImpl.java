package com.marketplace.hexagonal.Application.User.UseCases;

import com.marketplace.hexagonal.Domain.User.Exceptions.AddressNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.UserAddressId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.In.DeleteAddressUseCase;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserAddressRepositoryPort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DeleteAddressUseCaseImpl implements DeleteAddressUseCase {

    private final UserAddressRepositoryPort userAddressRepositoryPort;

    @Override
    public void deleteAddress(UserId userId, UserAddressId addressId) {
        if (!userAddressRepositoryPort.existsByIdAndUserId(addressId, userId)) {
            throw new AddressNotFoundException(addressId);
        }
        userAddressRepositoryPort.delete(addressId);
    }
}
