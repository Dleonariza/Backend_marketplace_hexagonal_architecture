package com.marketplace.hexagonal.Application.Shipping.UseCases;

import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Shipping.Exceptions.ShippingOptionNotFoundException;
import com.marketplace.hexagonal.Domain.Shipping.Exceptions.ShippingOptionOwnershipException;
import com.marketplace.hexagonal.Domain.Shipping.Models.SellerShippingOption;
import com.marketplace.hexagonal.Domain.Shipping.Models.ShippingOptionId;
import com.marketplace.hexagonal.Domain.Shipping.Ports.In.ToggleShippingOptionUseCase;
import com.marketplace.hexagonal.Domain.Shipping.Ports.Out.SellerShippingOptionRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ToggleShippingOptionUseCaseImpl implements ToggleShippingOptionUseCase {

    private final SellerShippingOptionRepositoryPort shippingRepo;

    @Override
    public void activateShippingOption(SellerProfileId sellerId, String optionId) {
        SellerShippingOption option = findAndCheckOwnership(sellerId, optionId);
        option.activate();
        shippingRepo.save(option);
    }

    @Override
    public void deactivateShippingOption(SellerProfileId sellerId, String optionId) {
        SellerShippingOption option = findAndCheckOwnership(sellerId, optionId);
        option.deactivate();
        shippingRepo.save(option);
    }

    private SellerShippingOption findAndCheckOwnership(SellerProfileId sellerId, String optionId) {
        SellerShippingOption option = shippingRepo
                .findById(ShippingOptionId.of(optionId))
                .orElseThrow(() -> new ShippingOptionNotFoundException(optionId));
        if (!option.getSellerId().equals(sellerId))
            throw new ShippingOptionOwnershipException();
        return option;
    }
}
