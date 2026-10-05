package com.marketplace.hexagonal.Domain.User.Ports.In;

import com.marketplace.hexagonal.Domain.User.Models.PaymentMethod;
import com.marketplace.hexagonal.Domain.User.Models.PaymentMethodId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public interface SetDefaultPaymentMethodUseCase {
    PaymentMethod setDefaultPaymentMethod(UserId userId, PaymentMethodId paymentMethodId);
}
