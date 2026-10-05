package com.marketplace.hexagonal.Domain.Shipping.Models;

import com.marketplace.hexagonal.Domain.Product.Models.Money;

import java.util.List;

public record ShippingQuote(
        String sellerId,
        String storeName,
        Money itemsSubtotal,
        List<SellerShippingOption> options
) {}
