package com.marketplace.hexagonal.Domain.Shipping.Ports.In;

import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

public interface RemoveShippingOptionUseCase {
    void removeShippingOption(SellerProfileId sellerId, String optionId);
}
