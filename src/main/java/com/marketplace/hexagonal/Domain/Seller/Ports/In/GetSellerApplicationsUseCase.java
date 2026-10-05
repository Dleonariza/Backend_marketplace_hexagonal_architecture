package com.marketplace.hexagonal.Domain.Seller.Ports.In;

import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;

import java.util.List;

public interface GetSellerApplicationsUseCase {
    List<SellerProfile> getPendingApplications();
    List<SellerProfile> getAllSellers();
}
