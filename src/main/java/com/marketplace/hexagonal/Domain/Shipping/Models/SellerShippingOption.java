package com.marketplace.hexagonal.Domain.Shipping.Models;

import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import lombok.Getter;

import java.time.Instant;

@Getter
public class SellerShippingOption {

    private final ShippingOptionId id;
    private final SellerProfileId sellerId;
    private String name;
    private String description;
    private Money price;
    private int estimatedDaysMin;
    private int estimatedDaysMax;
    private boolean active;
    private final Instant createdAt;

    private SellerShippingOption(ShippingOptionId id, SellerProfileId sellerId,
                                  String name, String description, Money price,
                                  int estimatedDaysMin, int estimatedDaysMax,
                                  boolean active, Instant createdAt) {
        this.id = id;
        this.sellerId = sellerId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.estimatedDaysMin = estimatedDaysMin;
        this.estimatedDaysMax = estimatedDaysMax;
        this.active = active;
        this.createdAt = createdAt;
    }

    public static SellerShippingOption create(SellerProfileId sellerId,
                                               String name,
                                               String description,
                                               Money price,
                                               int estimatedDaysMin,
                                               int estimatedDaysMax) {
        validate(name, price, estimatedDaysMin, estimatedDaysMax);
        return new SellerShippingOption(
                ShippingOptionId.generate(), sellerId,
                name, description, price,
                estimatedDaysMin, estimatedDaysMax,
                true, Instant.now());
    }

    public static SellerShippingOption reconstitute(ShippingOptionId id,
                                                     SellerProfileId sellerId,
                                                     String name,
                                                     String description,
                                                     Money price,
                                                     int estimatedDaysMin,
                                                     int estimatedDaysMax,
                                                     boolean active,
                                                     Instant createdAt) {
        return new SellerShippingOption(
                id, sellerId, name, description, price,
                estimatedDaysMin, estimatedDaysMax, active, createdAt);
    }

    public void update(String name, String description, Money price,
                       int estimatedDaysMin, int estimatedDaysMax) {
        validate(name, price, estimatedDaysMin, estimatedDaysMax);
        this.name = name;
        this.description = description;
        this.price = price;
        this.estimatedDaysMin = estimatedDaysMin;
        this.estimatedDaysMax = estimatedDaysMax;
    }

    public void activate() { this.active = true; }
    public void deactivate() { this.active = false; }

    private static void validate(String name, Money price, int min, int max) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Shipping option name cannot be blank");
        if (price.amount().signum() < 0)
            throw new IllegalArgumentException("Shipping price cannot be negative");
        if (min < 0)
            throw new IllegalArgumentException("Estimated days cannot be negative");
        if (max < min)
            throw new IllegalArgumentException("estimatedDaysMax must be >= estimatedDaysMin");
    }
}
