package com.marketplace.hexagonal.Infrastructura.DTO.Shipping;

import com.marketplace.hexagonal.Domain.Shipping.Models.ShippingQuote;

import java.math.BigDecimal;
import java.util.List;

public record ShippingQuoteResponseDTO(
        String sellerId,
        String storeName,
        BigDecimal itemsSubtotal,
        String currency,
        List<ShippingOptionResponseDTO> options
) {
    public static ShippingQuoteResponseDTO from(ShippingQuote quote) {
        return new ShippingQuoteResponseDTO(
                quote.sellerId(),
                quote.storeName(),
                quote.itemsSubtotal().amount(),
                quote.itemsSubtotal().currency(),
                quote.options().stream()
                        .map(ShippingOptionResponseDTO::from)
                        .toList());
    }
}
