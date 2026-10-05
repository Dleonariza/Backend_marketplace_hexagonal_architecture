package com.marketplace.hexagonal.Domain.Shipping.Ports.In;

import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

public interface ToggleShippingOptionUseCase {
    void activateShippingOption(SellerProfileId sellerId, String optionId);
    void deactivateShippingOption(SellerProfileId sellerId, String optionId);
}
