package com.marketplace.hexagonal.Domain.Seller.Ports.In;

import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.util.Optional;

public interface GetSellerProfileUseCase {
    SellerProfile getById(SellerProfileId id);
    Optional<SellerProfile> findByUserId(UserId userId);
}
