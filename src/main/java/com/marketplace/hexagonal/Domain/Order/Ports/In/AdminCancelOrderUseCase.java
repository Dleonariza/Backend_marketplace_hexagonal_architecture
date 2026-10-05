package com.marketplace.hexagonal.Domain.Order.Ports.In;

import com.marketplace.hexagonal.Domain.Order.Models.Order;

public interface AdminCancelOrderUseCase {
    Order execute(String orderId);
}
