package com.marketplace.hexagonal.Domain.Order.Ports.Out;

import com.marketplace.hexagonal.Domain.Order.Models.SubOrder;
import com.marketplace.hexagonal.Domain.Order.Models.SubOrderId;
import com.marketplace.hexagonal.Domain.Order.Models.OrderId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

import java.util.List;
import java.util.Optional;

public interface SubOrderRepositoryPort {

    SubOrder save(SubOrder subOrder);

    List<SubOrder> saveAll(List<SubOrder> subOrders);

    Optional<SubOrder> findById(SubOrderId id);

    List<SubOrder> findByOrderId(OrderId orderId);

    List<SubOrder> findBySellerIdOrderByCreatedAtDesc(SellerProfileId sellerId);
}
