package com.marketplace.hexagonal.Infrastructura.DTO.Shipping;

import com.marketplace.hexagonal.Domain.Shipping.Models.SellerShippingOption;

import java.math.BigDecimal;
import java.time.Instant;

public record ShippingOptionResponseDTO(
        String id,
        String name,
        String description,
        BigDecimal price,
        String currency,
        int estimatedDaysMin,
        int estimatedDaysMax,
        boolean active,
        Instant createdAt
) {
    public static ShippingOptionResponseDTO from(SellerShippingOption option) {
        return new ShippingOptionResponseDTO(
                option.getId().getValue(),
                option.getName(),
                option.getDescription(),
                option.getPrice().amount(),
                option.getPrice().currency(),
                option.getEstimatedDaysMin(),
                option.getEstimatedDaysMax(),
                option.isActive(),
                option.getCreatedAt());
    }
}
