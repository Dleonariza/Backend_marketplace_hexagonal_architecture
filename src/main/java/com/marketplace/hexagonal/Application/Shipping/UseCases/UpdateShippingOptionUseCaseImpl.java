package com.marketplace.hexagonal.Application.Shipping.UseCases;

import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Shipping.Exceptions.ShippingOptionNotFoundException;
import com.marketplace.hexagonal.Domain.Shipping.Exceptions.ShippingOptionOwnershipException;
import com.marketplace.hexagonal.Domain.Shipping.Models.SellerShippingOption;
import com.marketplace.hexagonal.Domain.Shipping.Models.ShippingOptionId;
import com.marketplace.hexagonal.Domain.Shipping.Ports.In.UpdateShippingOptionUseCase;
import com.marketplace.hexagonal.Domain.Shipping.Ports.Out.SellerShippingOptionRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateShippingOptionUseCaseImpl implements UpdateShippingOptionUseCase {

    private final SellerShippingOptionRepositoryPort shippingRepo;

    @Override
    public SellerShippingOption updateShippingOption(SellerProfileId sellerId, String optionId,
                                                      String name, String description, Money price,
                                                      int estimatedDaysMin, int estimatedDaysMax) {
        SellerShippingOption option = shippingRepo
                .findById(ShippingOptionId.of(optionId))
                .orElseThrow(() -> new ShippingOptionNotFoundException(optionId));

        if (!option.getSellerId().equals(sellerId))
            throw new ShippingOptionOwnershipException();

        option.update(name, description, price, estimatedDaysMin, estimatedDaysMax);
        return shippingRepo.save(option);
    }
}
