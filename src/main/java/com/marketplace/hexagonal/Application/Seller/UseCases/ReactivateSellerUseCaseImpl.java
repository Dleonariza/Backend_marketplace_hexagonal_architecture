package com.marketplace.hexagonal.Application.Seller.UseCases;

import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerProfileNotFoundException;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Seller.Ports.In.ReactivateSellerUseCase;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ReactivateSellerUseCaseImpl implements ReactivateSellerUseCase {

    private final SellerProfileRepositoryPort sellerProfileRepository;

    @Override
    public SellerProfile reactivate(SellerProfileId id, UserId adminId) {
        SellerProfile profile = sellerProfileRepository.findById(id)
                .orElseThrow(() -> new SellerProfileNotFoundException(id));

        profile.reactivate(adminId);
        return sellerProfileRepository.save(profile);
    }
}
