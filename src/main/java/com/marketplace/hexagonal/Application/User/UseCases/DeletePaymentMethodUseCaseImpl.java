package com.marketplace.hexagonal.Application.User.UseCases;

import com.marketplace.hexagonal.Domain.User.Exceptions.PaymentMethodNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.PaymentMethodId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.In.DeletePaymentMethodUseCase;
import com.marketplace.hexagonal.Domain.User.Ports.Out.PaymentMethodRepositoryPort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DeletePaymentMethodUseCaseImpl implements DeletePaymentMethodUseCase {

    private final PaymentMethodRepositoryPort paymentMethodRepositoryPort;

    @Override
    public void deletePaymentMethod(UserId userId, PaymentMethodId paymentMethodId) {
        if (!paymentMethodRepositoryPort.existsByIdAndUserId(paymentMethodId, userId)) {
            throw new PaymentMethodNotFoundException(paymentMethodId);
        }
        paymentMethodRepositoryPort.delete(paymentMethodId);
    }
}
