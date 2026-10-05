package com.marketplace.hexagonal.Application.Order.UseCases;

import com.marketplace.hexagonal.Domain.Order.Models.AdminOrderSearchCriteria;
import com.marketplace.hexagonal.Domain.Order.Models.Order;
import com.marketplace.hexagonal.Domain.Order.Ports.In.ListAllOrdersUseCase;
import com.marketplace.hexagonal.Domain.Order.Ports.Out.OrderRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Models.PageResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListAllOrdersUseCaseImpl implements ListAllOrdersUseCase {

    private final OrderRepositoryPort orderRepo;

    @Override
    public PageResult<Order> listAll(AdminOrderSearchCriteria criteria) {
        return orderRepo.findAllFiltered(criteria);
    }
}
