package com.marketplace.hexagonal.Application.User.UseCases;

import com.marketplace.hexagonal.Domain.User.Exceptions.AddressNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.UserAddress;
import com.marketplace.hexagonal.Domain.User.Models.UserAddressId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.In.SetDefaultAddressUseCase;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserAddressRepositoryPort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SetDefaultAddressUseCaseImpl implements SetDefaultAddressUseCase {

    private final UserAddressRepositoryPort userAddressRepositoryPort;

    @Override
    public UserAddress setDefault(UserId userId, UserAddressId addressId) {
        if (!userAddressRepositoryPort.existsByIdAndUserId(addressId, userId)) {
            throw new AddressNotFoundException(addressId);
        }

        UserAddress existing = userAddressRepositoryPort.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException(addressId));

        userAddressRepositoryPort.clearDefaultByUserId(userId);

        UserAddress toSave = UserAddress.builder()
                .id(addressId)
                .street(existing.getStreet())
                .city(existing.getCity())
                .state(existing.getState())
                .postalCode(existing.getPostalCode())
                .country(existing.getCountry())
                .isDefault(true)
                .build();

        return userAddressRepositoryPort.save(toSave, userId);
    }
}
