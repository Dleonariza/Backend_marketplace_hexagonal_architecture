package com.marketplace.hexagonal.Domain.Shipping.Ports.In;

import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Shipping.Models.SellerShippingOption;

import java.util.List;

public interface ListSellerShippingOptionsUseCase {
    List<SellerShippingOption> listShippingOptions(SellerProfileId sellerId);
}
