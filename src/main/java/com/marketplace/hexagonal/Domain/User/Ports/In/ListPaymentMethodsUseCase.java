package com.marketplace.hexagonal.Domain.User.Ports.In;

import com.marketplace.hexagonal.Domain.User.Models.PaymentMethod;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.util.List;

public interface ListPaymentMethodsUseCase {
    List<PaymentMethod> listPaymentMethods(UserId userId);
}
