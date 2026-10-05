package com.marketplace.hexagonal.Application.User.UseCases;

import com.marketplace.hexagonal.Domain.User.Exceptions.AddressNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.UserAddress;
import com.marketplace.hexagonal.Domain.User.Models.UserAddressId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.In.UpdateAddressUseCase;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserAddressRepositoryPort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateAddressUseCaseImpl implements UpdateAddressUseCase {

    private final UserAddressRepositoryPort userAddressRepositoryPort;

    @Override
    public UserAddress updateAddress(UserId userId, UserAddressId addressId, UserAddress updated) {
        if (!userAddressRepositoryPort.existsByIdAndUserId(addressId, userId)) {
            throw new AddressNotFoundException(addressId);
        }

        UserAddress existing = userAddressRepositoryPort.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException(addressId));

        boolean settingDefault = Boolean.TRUE.equals(updated.getIsDefault());
        if (settingDefault) {
            userAddressRepositoryPort.clearDefaultByUserId(userId);
        }

        UserAddress toSave = UserAddress.builder()
                .id(addressId)
                .street(updated.getStreet() != null ? updated.getStreet() : existing.getStreet())
                .city(updated.getCity() != null ? updated.getCity() : existing.getCity())
                .state(updated.getState() != null ? updated.getState() : existing.getState())
                .postalCode(updated.getPostalCode() != null ? updated.getPostalCode() : existing.getPostalCode())
                .country(updated.getCountry() != null ? updated.getCountry() : existing.getCountry())
                .isDefault(updated.getIsDefault() != null ? updated.getIsDefault() : existing.getIsDefault())
                .build();

        return userAddressRepositoryPort.save(toSave, userId);
    }
}
