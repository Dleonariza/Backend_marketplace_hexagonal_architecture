package com.marketplace.hexagonal.Application.User.UseCases;

import com.marketplace.hexagonal.Domain.User.Models.UserAddress;
import com.marketplace.hexagonal.Domain.User.Models.UserAddressId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.In.AddAddressUseCase;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserAddressRepositoryPort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AddAddressUseCaseImpl implements AddAddressUseCase {

    private final UserAddressRepositoryPort userAddressRepositoryPort;

    @Override
    public UserAddress addAddress(UserId userId, UserAddress address) {
        boolean isDefault = Boolean.TRUE.equals(address.getIsDefault());

        if (isDefault) {
            userAddressRepositoryPort.clearDefaultByUserId(userId);
        }

        boolean hasAddresses = !userAddressRepositoryPort.findAllByUserId(userId).isEmpty();
        boolean setAsDefault = isDefault || !hasAddresses;

        UserAddress toSave = UserAddress.builder()
                .id(UserAddressId.generate())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .isDefault(setAsDefault)
                .build();

        return userAddressRepositoryPort.save(toSave, userId);
    }
}
