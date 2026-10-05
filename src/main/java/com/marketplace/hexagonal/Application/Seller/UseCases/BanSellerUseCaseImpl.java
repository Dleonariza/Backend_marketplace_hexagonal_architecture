package com.marketplace.hexagonal.Application.Seller.UseCases;

import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerProfileNotFoundException;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Seller.Ports.In.BanSellerUseCase;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BanSellerUseCaseImpl implements BanSellerUseCase {

    private final SellerProfileRepositoryPort sellerProfileRepository;

    @Override
    public SellerProfile ban(SellerProfileId id, UserId adminId, String reason) {
        SellerProfile profile = sellerProfileRepository.findById(id)
                .orElseThrow(() -> new SellerProfileNotFoundException(id));

        profile.ban(adminId, reason);
        return sellerProfileRepository.save(profile);
    }
}
