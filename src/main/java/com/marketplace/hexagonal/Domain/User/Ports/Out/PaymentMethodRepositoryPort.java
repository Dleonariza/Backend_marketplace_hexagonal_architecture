package com.marketplace.hexagonal.Domain.User.Ports.Out;

import com.marketplace.hexagonal.Domain.User.Models.PaymentMethod;
import com.marketplace.hexagonal.Domain.User.Models.PaymentMethodId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodRepositoryPort {
    PaymentMethod save(PaymentMethod paymentMethod);
    Optional<PaymentMethod> findById(PaymentMethodId id);
    List<PaymentMethod> findAllByUserId(UserId userId);
    void delete(PaymentMethodId id);
    void clearDefaultByUserId(UserId userId);
    boolean existsByIdAndUserId(PaymentMethodId id, UserId userId);
}
