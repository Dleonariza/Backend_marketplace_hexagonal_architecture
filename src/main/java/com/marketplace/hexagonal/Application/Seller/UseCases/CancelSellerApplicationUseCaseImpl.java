package com.marketplace.hexagonal.Application.Seller.UseCases;

import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerProfileNotFoundException;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Ports.In.CancelSellerApplicationUseCase;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CancelSellerApplicationUseCaseImpl implements CancelSellerApplicationUseCase {

    private final SellerProfileRepositoryPort sellerProfileRepository;

    @Override
    public void cancelApplication(UserId userId) {
        SellerProfile profile = sellerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new SellerProfileNotFoundException(
                        "No active seller application found for the current user."));

        profile.cancel();
        sellerProfileRepository.save(profile);
    }
}
