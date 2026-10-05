package com.marketplace.hexagonal.Application.Seller.UseCases;

import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerProfileNotFoundException;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Seller.Ports.In.GetSellerProfileUseCase;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class GetSellerProfileUseCaseImpl implements GetSellerProfileUseCase {

    private final SellerProfileRepositoryPort sellerProfileRepository;

    @Override
    public SellerProfile getById(SellerProfileId id) {
        return sellerProfileRepository.findById(id)
                .orElseThrow(() -> new SellerProfileNotFoundException(id));
    }

    @Override
    public Optional<SellerProfile> findByUserId(UserId userId) {
        return sellerProfileRepository.findByUserId(userId);
    }
}
