package com.marketplace.hexagonal.Domain.Shipping.Ports.In;

import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Shipping.Models.SellerShippingOption;

public interface AddShippingOptionUseCase {
    SellerShippingOption addShippingOption(SellerProfileId sellerId, String name,
                                           String description, Money price,
                                           int estimatedDaysMin, int estimatedDaysMax);
}
