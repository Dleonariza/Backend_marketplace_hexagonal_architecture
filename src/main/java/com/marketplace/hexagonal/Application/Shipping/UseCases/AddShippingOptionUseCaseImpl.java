package com.marketplace.hexagonal.Application.Shipping.UseCases;

import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Shipping.Models.SellerShippingOption;
import com.marketplace.hexagonal.Domain.Shipping.Ports.In.AddShippingOptionUseCase;
import com.marketplace.hexagonal.Domain.Shipping.Ports.Out.SellerShippingOptionRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddShippingOptionUseCaseImpl implements AddShippingOptionUseCase {

    private final SellerShippingOptionRepositoryPort shippingRepo;

    @Override
    public SellerShippingOption addShippingOption(SellerProfileId sellerId, String name,
                                                   String description, Money price,
                                                   int estimatedDaysMin, int estimatedDaysMax) {
        SellerShippingOption option = SellerShippingOption.create(
                sellerId, name, description, price, estimatedDaysMin, estimatedDaysMax);
        return shippingRepo.save(option);
    }
}
