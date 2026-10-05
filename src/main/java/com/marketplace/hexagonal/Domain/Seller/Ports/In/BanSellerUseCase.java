package com.marketplace.hexagonal.Domain.Seller.Ports.In;

import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public interface BanSellerUseCase {

    SellerProfile ban(SellerProfileId id, UserId adminId, String reason);
}
