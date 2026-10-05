package com.marketplace.hexagonal.Domain.Seller.Ports.Out;

import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerStatus;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.util.List;
import java.util.Optional;

public interface SellerProfileRepositoryPort {
    SellerProfile save(SellerProfile profile);
    Optional<SellerProfile> findById(SellerProfileId id);
    Optional<SellerProfile> findByUserId(UserId userId);
    List<SellerProfile> findByStatus(SellerStatus status);
    List<SellerProfile> findAll();
}
