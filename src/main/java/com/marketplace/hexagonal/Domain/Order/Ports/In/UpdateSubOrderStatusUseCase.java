package com.marketplace.hexagonal.Domain.Order.Ports.In;

import com.marketplace.hexagonal.Domain.Order.Models.SubOrder;
import com.marketplace.hexagonal.Domain.Order.Models.SubOrderStatus;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

public interface UpdateSubOrderStatusUseCase {

    SubOrder execute(SellerProfileId sellerId, String subOrderId, SubOrderStatus newStatus);
}
