package com.marketplace.hexagonal.Application.Seller.UseCases;

import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerAlreadyAppliedException;
import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerBannedException;
import com.marketplace.hexagonal.Domain.Seller.Models.*;
import com.marketplace.hexagonal.Domain.Seller.Ports.In.ApplySellerUseCase;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class ApplySellerUseCaseImpl implements ApplySellerUseCase {

    private final SellerProfileRepositoryPort sellerProfileRepository;

    @Override
    public SellerProfile apply(UserId userId,
                               String storeName,
                               String storeDescription,
                               String mainCategory,
                               String contactEmail,
                               String contactPhone,
                               String logoUrl,
                               IdentityDocument identityDocument,
                               BankingInfo bankingInfo) {
        Optional<SellerProfile> existing = sellerProfileRepository.findByUserId(userId);

        if (existing.isPresent()) {
            SellerProfile current = existing.get();

            switch (current.getStatus()) {
                case BANNED ->
                    throw new SellerBannedException();

                case REJECTED, CANCELLED -> {
                    current.reapply(storeName, storeDescription, mainCategory,
                            contactEmail, contactPhone, logoUrl,
                            identityDocument, bankingInfo);
                    return sellerProfileRepository.save(current);
                }

                default ->

                    throw new SellerAlreadyAppliedException();
            }
        }

        SellerProfile newProfile = SellerProfile.create(
                userId, storeName, storeDescription, mainCategory,
                contactEmail, contactPhone, logoUrl,
                identityDocument, bankingInfo);

        return sellerProfileRepository.save(newProfile);
    }
}
