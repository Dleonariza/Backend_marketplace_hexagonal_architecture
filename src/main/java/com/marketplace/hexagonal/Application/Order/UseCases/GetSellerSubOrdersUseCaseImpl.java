package com.marketplace.hexagonal.Application.Order.UseCases;

import com.marketplace.hexagonal.Domain.Order.Models.SubOrder;
import com.marketplace.hexagonal.Domain.Order.Ports.In.GetSellerSubOrdersUseCase;
import com.marketplace.hexagonal.Domain.Order.Ports.Out.SubOrderRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetSellerSubOrdersUseCaseImpl implements GetSellerSubOrdersUseCase {

    private final SubOrderRepositoryPort subOrderRepo;

    @Override
    public List<SubOrder> execute(SellerProfileId sellerId) {
        return subOrderRepo.findBySellerIdOrderByCreatedAtDesc(sellerId);
    }
}
