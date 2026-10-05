package com.marketplace.hexagonal.Domain.User.Models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserAddressId {
    private final String value;

    public UserAddressId(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("AddressId can't null or blank");
        this.value = value;
    }

    public static UserAddressId generate(){
        return new UserAddressId(UUID.randomUUID().toString());
    }

    public static UserAddressId of(String value){return new UserAddressId(value);}
}
