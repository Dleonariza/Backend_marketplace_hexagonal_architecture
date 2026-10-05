package com.marketplace.hexagonal.Domain.Order.Ports.In;

import com.marketplace.hexagonal.Domain.Order.Models.SubOrder;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

import java.util.List;

public interface GetSellerSubOrdersUseCase {

    List<SubOrder> execute(SellerProfileId sellerId);
}
