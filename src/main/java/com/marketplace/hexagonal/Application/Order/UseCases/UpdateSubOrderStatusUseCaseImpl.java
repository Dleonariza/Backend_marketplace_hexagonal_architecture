package com.marketplace.hexagonal.Application.Order.UseCases;

import com.marketplace.hexagonal.Domain.Order.Exceptions.SubOrderAccessDeniedException;
import com.marketplace.hexagonal.Domain.Order.Exceptions.SubOrderNotFoundException;
import com.marketplace.hexagonal.Domain.Order.Models.SubOrder;
import com.marketplace.hexagonal.Domain.Order.Models.SubOrderId;
import com.marketplace.hexagonal.Domain.Order.Models.SubOrderStatus;
import com.marketplace.hexagonal.Domain.Order.Ports.In.UpdateSubOrderStatusUseCase;
import com.marketplace.hexagonal.Domain.Order.Ports.Out.SubOrderRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateSubOrderStatusUseCaseImpl implements UpdateSubOrderStatusUseCase {

    private final SubOrderRepositoryPort subOrderRepo;

    @Override
    public SubOrder execute(SellerProfileId sellerId, String subOrderId, SubOrderStatus newStatus) {
        SubOrder subOrder = subOrderRepo.findById(SubOrderId.of(subOrderId))
                .orElseThrow(() -> new SubOrderNotFoundException(subOrderId));

        if (!subOrder.getSellerId().equals(sellerId))
            throw new SubOrderAccessDeniedException();

        switch (newStatus) {
            case PROCESSING -> subOrder.markProcessing();
            case SHIPPED -> subOrder.markShipped();
            case DELIVERED -> subOrder.markDelivered();
            case CANCELLED -> subOrder.cancel();
            default -> throw new IllegalArgumentException(
                    "Cannot manually set status to: " + newStatus);
        }

        return subOrderRepo.save(subOrder);
    }
}
