package com.marketplace.hexagonal.Domain.Shipping.Ports.Out;

import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Shipping.Models.SellerShippingOption;
import com.marketplace.hexagonal.Domain.Shipping.Models.ShippingOptionId;

import java.util.List;
import java.util.Optional;

public interface SellerShippingOptionRepositoryPort {
    SellerShippingOption save(SellerShippingOption option);
    Optional<SellerShippingOption> findById(ShippingOptionId id);
    List<SellerShippingOption> findBySellerId(SellerProfileId sellerId);
    List<SellerShippingOption> findActivesBySellerId(SellerProfileId sellerId);
    void deleteById(ShippingOptionId id);
}
