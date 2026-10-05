package com.marketplace.hexagonal.Domain.User.Ports.In;

import com.marketplace.hexagonal.Domain.User.Models.PaymentMethod;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public interface AddPaymentMethodUseCase {
    PaymentMethod addPaymentMethod(UserId userId, PaymentMethod paymentMethod);
}
