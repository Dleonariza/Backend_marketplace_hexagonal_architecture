package com.marketplace.hexagonal.Domain.Seller.Events;

import com.marketplace.hexagonal.Domain.Seller.Models.SellerStatus;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public record SellerVerificationCompletedEvent(
        UserId sellerUserId,
        String storeName,
        SellerStatus newStatus,
        String reason
) {}
