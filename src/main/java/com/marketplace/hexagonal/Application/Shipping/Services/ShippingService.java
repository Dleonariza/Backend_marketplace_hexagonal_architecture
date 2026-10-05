package com.marketplace.hexagonal.Application.Shipping.Services;

import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Shipping.Models.SellerShippingOption;
import com.marketplace.hexagonal.Domain.Shipping.Models.ShippingQuote;
import com.marketplace.hexagonal.Domain.Shipping.Ports.In.*;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ShippingService implements
        AddShippingOptionUseCase,
        UpdateShippingOptionUseCase,
        RemoveShippingOptionUseCase,
        ToggleShippingOptionUseCase,
        ListSellerShippingOptionsUseCase,
        CalculateShippingUseCase {
    private final AddShippingOptionUseCase addUseCase;
    private final UpdateShippingOptionUseCase updateUseCase;
    private final RemoveShippingOptionUseCase removeUseCase;
    private final ToggleShippingOptionUseCase toggleUseCase;
    private final ListSellerShippingOptionsUseCase listUseCase;
    private final CalculateShippingUseCase calculateUseCase;

    @Override
    public SellerShippingOption addShippingOption(SellerProfileId sellerId, String name,
                                                   String description, Money price,
                                                   int estimatedDaysMin, int estimatedDaysMax) {
        return addUseCase.addShippingOption(sellerId, name, description, price, estimatedDaysMin, estimatedDaysMax);
    }

    @Override
    public SellerShippingOption updateShippingOption(SellerProfileId sellerId, String optionId,
                                                      String name, String description, Money price,
                                                      int estimatedDaysMin, int estimatedDaysMax) {
        return updateUseCase.updateShippingOption(sellerId, optionId, name, description, price, estimatedDaysMin, estimatedDaysMax);
    }

    @Override
    public void removeShippingOption(SellerProfileId sellerId, String optionId) {
        removeUseCase.removeShippingOption(sellerId, optionId);
    }

    @Override
    public void activateShippingOption(SellerProfileId sellerId, String optionId) {
        toggleUseCase.activateShippingOption(sellerId, optionId);
    }

    @Override
    public void deactivateShippingOption(SellerProfileId sellerId, String optionId) {
        toggleUseCase.deactivateShippingOption(sellerId, optionId);
    }

    @Override
    public List<SellerShippingOption> listShippingOptions(SellerProfileId sellerId) {
        return listUseCase.listShippingOptions(sellerId);
    }

    @Override
    public List<ShippingQuote> calculateShipping(UserId userId, String sessionId) {
        return calculateUseCase.calculateShipping(userId, sessionId);
    }
}
