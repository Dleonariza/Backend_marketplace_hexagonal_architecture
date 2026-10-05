package com.marketplace.hexagonal.Infrastructura.DTO.Address;

import com.marketplace.hexagonal.Domain.User.Models.UserAddress;

public record AddressResponseDTO(
        String id,
        String street,
        String city,
        String state,
        String postalCode,
        String country,
        Boolean isDefault
) {
    public static AddressResponseDTO from(UserAddress address) {
        return new AddressResponseDTO(
                address.getId().getValue(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry(),
                address.getIsDefault()
        );
    }
}
