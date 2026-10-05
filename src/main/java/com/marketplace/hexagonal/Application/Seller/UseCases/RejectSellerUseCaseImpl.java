package com.marketplace.hexagonal.Application.Seller.UseCases;

import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerProfileNotFoundException;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Seller.Ports.In.RejectSellerUseCase;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RejectSellerUseCaseImpl implements RejectSellerUseCase {

    private final SellerProfileRepositoryPort sellerProfileRepository;

    @Override
    public SellerProfile reject(SellerProfileId id, UserId adminId, String reason) {
        SellerProfile profile = sellerProfileRepository.findById(id)
                .orElseThrow(() -> new SellerProfileNotFoundException(id));

        profile.reject(adminId, reason);
        return sellerProfileRepository.save(profile);
    }
}
