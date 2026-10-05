package com.marketplace.hexagonal.Infrastructura.DTO.Order;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record PlaceOrderRequestDTO(

        @NotBlank(message = "shippingAddressId must not be blank")
        String shippingAddressId,

        Map<String, String> shippingSelections,

        String paymentMethodId
) {}
