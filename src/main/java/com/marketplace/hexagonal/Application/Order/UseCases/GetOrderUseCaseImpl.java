package com.marketplace.hexagonal.Application.Order.UseCases;

import com.marketplace.hexagonal.Domain.Order.Exceptions.OrderAccessDeniedException;
import com.marketplace.hexagonal.Domain.Order.Exceptions.OrderNotFoundException;
import com.marketplace.hexagonal.Domain.Order.Models.Order;
import com.marketplace.hexagonal.Domain.Order.Ports.In.GetOrderUseCase;
import com.marketplace.hexagonal.Domain.Order.Ports.Out.OrderRepositoryPort;
import com.marketplace.hexagonal.Domain.Order.Models.OrderId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetOrderUseCaseImpl implements GetOrderUseCase {

    private final OrderRepositoryPort orderRepo;

    @Override
    public Order execute(UserId userId, String orderId) {
        Order order = orderRepo.findById(OrderId.of(orderId))
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!order.getUserId().equals(userId))
            throw new OrderAccessDeniedException();

        return order;
    }
}
