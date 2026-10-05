package com.marketplace.hexagonal.Domain.User.Ports.In;

import com.marketplace.hexagonal.Domain.User.Models.PaymentMethodId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public interface DeletePaymentMethodUseCase {
    void deletePaymentMethod(UserId userId, PaymentMethodId paymentMethodId);
}
