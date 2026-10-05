package com.marketplace.hexagonal.Domain.Order.Ports.In;

import com.marketplace.hexagonal.Domain.Order.Models.Order;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.util.Map;

public interface PlaceOrderUseCase {

    Order execute(UserId userId,
                  String idempotencyKey,
                  String shippingAddressId,
                  Map<String, String> shippingSelections,
                  String paymentMethodId);
}
