package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.User.Exceptions.PaymentMethodNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.PaymentMethod;
import com.marketplace.hexagonal.Domain.User.Models.PaymentMethodId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.Out.PaymentMethodRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Entities.User.PaymentMethodEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.User.UserEntity;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaPaymentMethodRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class JpaPaymentMethodRepositoryAdapter implements PaymentMethodRepositoryPort {

    private final JpaPaymentMethodRepository jpaPaymentMethodRepository;
    private final JpaUserRepository jpaUserRepository;

    @Override
    @Transactional
    public PaymentMethod save(PaymentMethod paymentMethod) {
        UserEntity userEntity = jpaUserRepository
                .findById(UUID.fromString(paymentMethod.getUserId().getValue()))
                .orElseThrow(() -> new IllegalStateException(
                        "User not found: " + paymentMethod.getUserId().getValue()));

        UUID entityId = paymentMethod.getId() != null
                ? UUID.fromString(paymentMethod.getId().getValue())
                : null;

        PaymentMethodEntity entity = (entityId != null)
                ? jpaPaymentMethodRepository.findById(entityId).orElse(new PaymentMethodEntity())
                : new PaymentMethodEntity();

        entity.setStripePaymentMethodId(paymentMethod.getStripePaymentMethodId());
        entity.setLast4(paymentMethod.getLast4());
        entity.setBrand(paymentMethod.getBrand());
        entity.setExpMonth(paymentMethod.getExpMonth());
        entity.setExpYear(paymentMethod.getExpYear());
        entity.setCardholderName(paymentMethod.getCardholderName());
        entity.setDefault(paymentMethod.isDefault());
        entity.setCreatedAt(paymentMethod.getCreatedAt());
        entity.setUser(userEntity);

        return toDomain(jpaPaymentMethodRepository.save(entity), paymentMethod.getUserId());
    }

    @Override
    public Optional<PaymentMethod> findById(PaymentMethodId id) {
        return jpaPaymentMethodRepository.findById(UUID.fromString(id.getValue()))
                .map(e -> toDomain(e, UserId.of(e.getUser().getId().toString())));
    }

    @Override
    public List<PaymentMethod> findAllByUserId(UserId userId) {
        return jpaPaymentMethodRepository.findAllByUserId(UUID.fromString(userId.getValue()))
                .stream()
                .map(e -> toDomain(e, userId))
                .toList();
    }

    @Override
    @Transactional
    public void delete(PaymentMethodId id) {
        UUID uuid = UUID.fromString(id.getValue());
        if (!jpaPaymentMethodRepository.existsById(uuid)) {
            throw new PaymentMethodNotFoundException(id);
        }
        jpaPaymentMethodRepository.deletePaymentMethodById(uuid);
    }

    @Override
    @Transactional
    public void clearDefaultByUserId(UserId userId) {
        jpaPaymentMethodRepository.clearDefaultByUserId(UUID.fromString(userId.getValue()));
    }

    @Override
    public boolean existsByIdAndUserId(PaymentMethodId id, UserId userId) {
        return jpaPaymentMethodRepository.existsByIdAndUserId(
                UUID.fromString(id.getValue()),
                UUID.fromString(userId.getValue()));
    }

    private PaymentMethod toDomain(PaymentMethodEntity entity, UserId userId) {
        return PaymentMethod.builder()
                .id(PaymentMethodId.of(entity.getId().toString()))
                .userId(userId)
                .stripePaymentMethodId(entity.getStripePaymentMethodId())
                .last4(entity.getLast4())
                .brand(entity.getBrand())
                .expMonth(entity.getExpMonth())
                .expYear(entity.getExpYear())
                .cardholderName(entity.getCardholderName())
                .isDefault(entity.isDefault())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
