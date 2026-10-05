package com.marketplace.hexagonal.Domain.Order.Ports.In;

import com.marketplace.hexagonal.Domain.Order.Models.Order;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

public interface SellerCancelOrderUseCase {
    Order execute(SellerProfileId sellerId, String orderId);
}
