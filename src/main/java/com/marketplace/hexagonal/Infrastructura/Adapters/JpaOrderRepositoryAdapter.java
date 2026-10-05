package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.Order.Models.AdminOrderSearchCriteria;
import com.marketplace.hexagonal.Domain.Order.Models.Order;
import com.marketplace.hexagonal.Domain.Order.Models.OrderId;
import com.marketplace.hexagonal.Domain.Order.Models.SubOrder;
import com.marketplace.hexagonal.Domain.Order.Ports.Out.OrderRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Models.PageResult;
import com.marketplace.hexagonal.Domain.User.Exceptions.UserNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.Entities.Order.OrderEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.User.UserEntity;
import com.marketplace.hexagonal.Infrastructura.Mappers.OrderMapper;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaOrderRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaSubOrderRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaUserRepository;
import com.marketplace.hexagonal.Infrastructura.Specifications.OrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class JpaOrderRepositoryAdapter implements OrderRepositoryPort {

    private final JpaOrderRepository jpaOrderRepo;
    private final JpaUserRepository jpaUserRepo;
    private final JpaSubOrderRepository jpaSubOrderRepo;
    private final OrderMapper mapper;

    @Override
    public Order save(Order order) {
        UserEntity user = jpaUserRepo
                .findById(UUID.fromString(order.getUserId().getValue()))
                .orElseThrow(() -> new UserNotFoundException(order.getUserId().getValue()));

        OrderEntity entity = mapper.toEntity(order, user);
        return toDomain(jpaOrderRepo.save(entity));
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return jpaOrderRepo.findById(UUID.fromString(id.getValue()))
                .map(this::toDomain);
    }

    @Override
    public Optional<Order> findByUserIdAndIdempotencyKey(UserId userId, String idempotencyKey) {
        return jpaOrderRepo
                .findByUser_IdAndIdempotencyKey(
                        UUID.fromString(userId.getValue()), idempotencyKey)
                .map(this::toDomain);
    }

    @Override
    public List<Order> findByUserIdOrderByCreatedAtDesc(UserId userId) {
        return jpaOrderRepo
                .findByUser_IdOrderByCreatedAtDesc(UUID.fromString(userId.getValue()))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByNumOrder(String numOrder) {
        return jpaOrderRepo.existsByNumOrder(numOrder);
    }

    @Override
    public PageResult<Order> findAllFiltered(AdminOrderSearchCriteria criteria) {
        PageRequest pageable = PageRequest.of(
                criteria.page(), criteria.size(),
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<OrderEntity> page = jpaOrderRepo.findAll(
                OrderSpecification.fromCriteria(criteria), pageable);

        List<Order> orders = page.getContent().stream()
                .map(this::toDomain)
                .toList();

        return new PageResult<>(orders,
                page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(),
                page.isLast());
    }

    private Order toDomain(OrderEntity entity) {
        List<SubOrder> subOrders = jpaSubOrderRepo
                .findByOrder_IdOrderByCreatedAtDesc(entity.getId())
                .stream()
                .map(mapper::toSubOrderDomain)
                .toList();
        return mapper.toDomain(entity, subOrders);
    }
}
