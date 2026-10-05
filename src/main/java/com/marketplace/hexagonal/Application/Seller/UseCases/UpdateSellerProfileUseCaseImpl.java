package com.marketplace.hexagonal.Application.Seller.UseCases;

import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerProfileNotFoundException;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Ports.In.UpdateSellerProfileUseCase;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateSellerProfileUseCaseImpl implements UpdateSellerProfileUseCase {

    private final SellerProfileRepositoryPort sellerRepo;

    @Override
    public SellerProfile updateProfile(
            UserId userId,
            String storeName,
            String storeDescription,
            String mainCategory,
            String contactEmail,
            String contactPhone,
            String logoUrl) {
        SellerProfile profile = sellerRepo.findByUserId(userId)
                .orElseThrow(() -> new SellerProfileNotFoundException(
                        "No seller profile found for user: " + userId.getValue()));

        profile.updateStoreInfo(storeName, storeDescription, mainCategory,
                contactEmail, contactPhone, logoUrl);

        return sellerRepo.save(profile);
    }
}
