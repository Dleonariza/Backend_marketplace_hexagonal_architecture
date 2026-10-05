package com.marketplace.hexagonal.Application.Order.UseCases;

import com.marketplace.hexagonal.Domain.Order.Exceptions.OrderNotFoundException;
import com.marketplace.hexagonal.Domain.Order.Models.Order;
import com.marketplace.hexagonal.Domain.Order.Models.OrderId;
import com.marketplace.hexagonal.Domain.Order.Ports.In.AdminCancelOrderUseCase;
import com.marketplace.hexagonal.Domain.Order.Ports.Out.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminCancelOrderUseCaseImpl implements AdminCancelOrderUseCase {

    private final OrderRepositoryPort orderRepo;

    @Override
    public Order execute(String orderId) {
        Order order = orderRepo.findById(OrderId.of(orderId))
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.cancelAsPrivileged();
        return orderRepo.save(order);
    }
}
