package com.marketplace.hexagonal.Application.Order.UseCases;

import com.marketplace.hexagonal.Domain.Order.Exceptions.OrderAccessDeniedException;
import com.marketplace.hexagonal.Domain.Order.Exceptions.OrderNotFoundException;
import com.marketplace.hexagonal.Domain.Order.Models.Order;
import com.marketplace.hexagonal.Domain.Order.Models.OrderId;
import com.marketplace.hexagonal.Domain.Order.Ports.In.SellerCancelOrderUseCase;
import com.marketplace.hexagonal.Domain.Order.Ports.Out.OrderRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SellerCancelOrderUseCaseImpl implements SellerCancelOrderUseCase {

    private final OrderRepositoryPort orderRepo;

    @Override
    public Order execute(SellerProfileId sellerId, String orderId) {
        Order order = orderRepo.findById(OrderId.of(orderId))
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        boolean sellerOwnsSubOrder = order.getSubOrders().stream()
                .anyMatch(s -> s.getSellerId().equals(sellerId));
        if (!sellerOwnsSubOrder)
            throw new OrderAccessDeniedException();

        order.cancelAsPrivileged();
        return orderRepo.save(order);
    }
}
