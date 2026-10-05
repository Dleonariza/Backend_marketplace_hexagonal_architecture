package com.marketplace.hexagonal.Domain.Shipping.Ports.In;

import com.marketplace.hexagonal.Domain.Shipping.Models.ShippingQuote;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.util.List;

public interface CalculateShippingUseCase {
    List<ShippingQuote> calculateShipping(UserId userId, String sessionId);
}
