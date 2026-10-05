package com.marketplace.hexagonal.Application.Shipping.UseCases;

import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Shipping.Exceptions.ShippingOptionNotFoundException;
import com.marketplace.hexagonal.Domain.Shipping.Exceptions.ShippingOptionOwnershipException;
import com.marketplace.hexagonal.Domain.Shipping.Models.SellerShippingOption;
import com.marketplace.hexagonal.Domain.Shipping.Models.ShippingOptionId;
import com.marketplace.hexagonal.Domain.Shipping.Ports.In.RemoveShippingOptionUseCase;
import com.marketplace.hexagonal.Domain.Shipping.Ports.Out.SellerShippingOptionRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RemoveShippingOptionUseCaseImpl implements RemoveShippingOptionUseCase {

    private final SellerShippingOptionRepositoryPort shippingRepo;

    @Override
    public void removeShippingOption(SellerProfileId sellerId, String optionId) {
        SellerShippingOption option = shippingRepo
                .findById(ShippingOptionId.of(optionId))
                .orElseThrow(() -> new ShippingOptionNotFoundException(optionId));

        if (!option.getSellerId().equals(sellerId))
            throw new ShippingOptionOwnershipException();

        shippingRepo.deleteById(ShippingOptionId.of(optionId));
    }
}
