package com.marketplace.hexagonal.Application.User.Services;

import com.marketplace.hexagonal.Domain.User.Models.UserAddress;
import com.marketplace.hexagonal.Domain.User.Models.UserAddressId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.In.*;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class AddressService implements
        GetAddressesUseCase,
        AddAddressUseCase,
        UpdateAddressUseCase,
        DeleteAddressUseCase,
        SetDefaultAddressUseCase {
    private final GetAddressesUseCase getAddressesUseCase;
    private final AddAddressUseCase addAddressUseCase;
    private final UpdateAddressUseCase updateAddressUseCase;
    private final DeleteAddressUseCase deleteAddressUseCase;
    private final SetDefaultAddressUseCase setDefaultAddressUseCase;

    @Override
    public List<UserAddress> getAddresses(UserId userId) {
        return getAddressesUseCase.getAddresses(userId);
    }

    @Override
    public UserAddress addAddress(UserId userId, UserAddress address) {
        return addAddressUseCase.addAddress(userId, address);
    }

    @Override
    public UserAddress updateAddress(UserId userId, UserAddressId addressId, UserAddress updated) {
        return updateAddressUseCase.updateAddress(userId, addressId, updated);
    }

    @Override
    public void deleteAddress(UserId userId, UserAddressId addressId) {
        deleteAddressUseCase.deleteAddress(userId, addressId);
    }

    @Override
    public UserAddress setDefault(UserId userId, UserAddressId addressId) {
        return setDefaultAddressUseCase.setDefault(userId, addressId);
    }
}
