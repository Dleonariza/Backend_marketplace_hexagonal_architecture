package com.marketplace.hexagonal.Application.Shipping.UseCases;

import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Shipping.Models.SellerShippingOption;
import com.marketplace.hexagonal.Domain.Shipping.Ports.In.ListSellerShippingOptionsUseCase;
import com.marketplace.hexagonal.Domain.Shipping.Ports.Out.SellerShippingOptionRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ListSellerShippingOptionsUseCaseImpl implements ListSellerShippingOptionsUseCase {

    private final SellerShippingOptionRepositoryPort shippingRepo;

    @Override
    public List<SellerShippingOption> listShippingOptions(SellerProfileId sellerId) {
        return shippingRepo.findBySellerId(sellerId);
    }
}
