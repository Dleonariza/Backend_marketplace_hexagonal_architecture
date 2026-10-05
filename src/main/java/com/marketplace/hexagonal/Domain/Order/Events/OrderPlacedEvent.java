package com.marketplace.hexagonal.Domain.Order.Events;

import com.marketplace.hexagonal.Domain.Order.Models.OrderId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public record OrderPlacedEvent(OrderId orderId, UserId userId) {}
