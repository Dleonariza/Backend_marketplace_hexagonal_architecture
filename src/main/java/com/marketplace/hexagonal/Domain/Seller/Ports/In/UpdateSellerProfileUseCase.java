package com.marketplace.hexagonal.Domain.Seller.Ports.In;

import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public interface UpdateSellerProfileUseCase {

    SellerProfile updateProfile(
            UserId userId,
            String storeName,
            String storeDescription,
            String mainCategory,
            String contactEmail,
            String contactPhone,
            String logoUrl);
}
