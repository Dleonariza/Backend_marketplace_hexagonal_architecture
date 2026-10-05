package com.marketplace.hexagonal.Domain.Order.Ports.In;

import com.marketplace.hexagonal.Domain.Order.Models.Order;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.util.List;

public interface ListUserOrdersUseCase {

    List<Order> execute(UserId userId);
}
