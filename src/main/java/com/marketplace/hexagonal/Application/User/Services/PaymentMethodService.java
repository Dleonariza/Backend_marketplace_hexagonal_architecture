package com.marketplace.hexagonal.Application.User.Services;

import com.marketplace.hexagonal.Domain.User.Models.PaymentMethod;
import com.marketplace.hexagonal.Domain.User.Models.PaymentMethodId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.In.*;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class PaymentMethodService implements
        AddPaymentMethodUseCase,
        ListPaymentMethodsUseCase,
        DeletePaymentMethodUseCase,
        SetDefaultPaymentMethodUseCase {
    private final AddPaymentMethodUseCase addPaymentMethodUseCase;
    private final ListPaymentMethodsUseCase listPaymentMethodsUseCase;
    private final DeletePaymentMethodUseCase deletePaymentMethodUseCase;
    private final SetDefaultPaymentMethodUseCase setDefaultPaymentMethodUseCase;

    @Override
    public PaymentMethod addPaymentMethod(UserId userId, PaymentMethod paymentMethod) {
        return addPaymentMethodUseCase.addPaymentMethod(userId, paymentMethod);
    }

    @Override
    public List<PaymentMethod> listPaymentMethods(UserId userId) {
        return listPaymentMethodsUseCase.listPaymentMethods(userId);
    }

    @Override
    public void deletePaymentMethod(UserId userId, PaymentMethodId paymentMethodId) {
        deletePaymentMethodUseCase.deletePaymentMethod(userId, paymentMethodId);
    }

    @Override
    public PaymentMethod setDefaultPaymentMethod(UserId userId, PaymentMethodId paymentMethodId) {
        return setDefaultPaymentMethodUseCase.setDefaultPaymentMethod(userId, paymentMethodId);
    }
}
