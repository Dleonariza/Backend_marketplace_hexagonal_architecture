package com.marketplace.hexagonal.Application.Order.UseCases;

import com.marketplace.hexagonal.Domain.Order.Models.Order;
import com.marketplace.hexagonal.Domain.Order.Ports.In.ListUserOrdersUseCase;
import com.marketplace.hexagonal.Domain.Order.Ports.Out.OrderRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ListUserOrdersUseCaseImpl implements ListUserOrdersUseCase {

    private final OrderRepositoryPort orderRepo;

    @Override
    public List<Order> execute(UserId userId) {
        return orderRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
