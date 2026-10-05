package com.marketplace.hexagonal.Domain.Seller.Ports.In;

import com.marketplace.hexagonal.Domain.User.Models.UserId;

public interface CancelSellerApplicationUseCase {

    void cancelApplication(UserId userId);
}
