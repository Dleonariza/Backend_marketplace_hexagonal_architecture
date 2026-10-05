package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.Order.Models.SubOrder;
import com.marketplace.hexagonal.Domain.Order.Models.SubOrderId;
import com.marketplace.hexagonal.Domain.Order.Models.OrderId;
import com.marketplace.hexagonal.Domain.Order.Ports.Out.SubOrderRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Infrastructura.Entities.Order.OrderEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Order.SubOrderEntity;
import com.marketplace.hexagonal.Infrastructura.Mappers.OrderMapper;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaOrderRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaSubOrderRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class JpaSubOrderRepositoryAdapter implements SubOrderRepositoryPort {

    private final JpaSubOrderRepository jpaSubOrderRepo;
    private final JpaOrderRepository jpaOrderRepo;
    private final OrderMapper mapper;

    @Override
    public SubOrder save(SubOrder subOrder) {
        OrderEntity orderEntity = jpaOrderRepo
                .findById(UUID.fromString(subOrder.getOrderId().getValue()))
                .orElseThrow();
        SubOrderEntity entity = mapper.toSubOrderEntity(subOrder, orderEntity);
        return mapper.toSubOrderDomain(jpaSubOrderRepo.save(entity));
    }

    @Override
    public List<SubOrder> saveAll(List<SubOrder> subOrders) {
        if (subOrders.isEmpty()) return List.of();

        OrderEntity orderEntity = jpaOrderRepo
                .findById(UUID.fromString(subOrders.getFirst().getOrderId().getValue()))
                .orElseThrow();

        List<SubOrderEntity> entities = subOrders.stream()
                .map(s -> mapper.toSubOrderEntity(s, orderEntity))
                .toList();
        return jpaSubOrderRepo.saveAll(entities).stream()
                .map(mapper::toSubOrderDomain)
                .toList();
    }

    @Override
    public Optional<SubOrder> findById(SubOrderId id) {
        return jpaSubOrderRepo.findById(UUID.fromString(id.getValue()))
                .map(mapper::toSubOrderDomain);
    }

    @Override
    public List<SubOrder> findByOrderId(OrderId orderId) {
        return jpaSubOrderRepo
                .findByOrder_IdOrderByCreatedAtDesc(UUID.fromString(orderId.getValue()))
                .stream()
                .map(mapper::toSubOrderDomain)
                .toList();
    }

    @Override
    public List<SubOrder> findBySellerIdOrderByCreatedAtDesc(SellerProfileId sellerId) {
        return jpaSubOrderRepo
                .findBySellerIdOrderByCreatedAtDesc(UUID.fromString(sellerId.getValue()))
                .stream()
                .map(mapper::toSubOrderDomain)
                .toList();
    }
}
