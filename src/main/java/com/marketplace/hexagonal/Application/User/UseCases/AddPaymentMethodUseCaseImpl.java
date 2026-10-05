package com.marketplace.hexagonal.Application.User.UseCases;

import com.marketplace.hexagonal.Domain.User.Models.PaymentMethod;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.In.AddPaymentMethodUseCase;
import com.marketplace.hexagonal.Domain.User.Ports.Out.PaymentMethodRepositoryPort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AddPaymentMethodUseCaseImpl implements AddPaymentMethodUseCase {

    private final PaymentMethodRepositoryPort paymentMethodRepositoryPort;

    @Override
    public PaymentMethod addPaymentMethod(UserId userId, PaymentMethod paymentMethod) {
        boolean requestedDefault = paymentMethod.isDefault();

        boolean hasCards = !paymentMethodRepositoryPort.findAllByUserId(userId).isEmpty();
        boolean setAsDefault = requestedDefault || !hasCards;

        if (setAsDefault) {
            paymentMethodRepositoryPort.clearDefaultByUserId(userId);
        }

        PaymentMethod toSave = PaymentMethod.create(
                userId,
                paymentMethod.getStripePaymentMethodId(),
                paymentMethod.getLast4(),
                paymentMethod.getBrand(),
                paymentMethod.getExpMonth(),
                paymentMethod.getExpYear(),
                paymentMethod.getCardholderName(),
                setAsDefault
        );

        return paymentMethodRepositoryPort.save(toSave);
    }
}
