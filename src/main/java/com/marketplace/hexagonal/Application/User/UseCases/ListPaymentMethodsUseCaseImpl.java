package com.marketplace.hexagonal.Application.User.UseCases;

import com.marketplace.hexagonal.Domain.User.Models.PaymentMethod;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.In.ListPaymentMethodsUseCase;
import com.marketplace.hexagonal.Domain.User.Ports.Out.PaymentMethodRepositoryPort;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ListPaymentMethodsUseCaseImpl implements ListPaymentMethodsUseCase {

    private final PaymentMethodRepositoryPort paymentMethodRepositoryPort;

    @Override
    public List<PaymentMethod> listPaymentMethods(UserId userId) {
        return paymentMethodRepositoryPort.findAllByUserId(userId);
    }
}
