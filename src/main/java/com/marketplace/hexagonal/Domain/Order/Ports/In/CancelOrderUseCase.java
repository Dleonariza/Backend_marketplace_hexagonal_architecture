package com.marketplace.hexagonal.Domain.Order.Ports.In;

import com.marketplace.hexagonal.Domain.Order.Models.Order;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public interface CancelOrderUseCase {

    Order execute(UserId userId, String orderId);
}
