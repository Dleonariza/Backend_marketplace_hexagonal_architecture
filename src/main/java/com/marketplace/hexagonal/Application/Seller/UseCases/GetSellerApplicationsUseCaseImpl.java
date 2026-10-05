package com.marketplace.hexagonal.Application.Seller.UseCases;

import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerStatus;
import com.marketplace.hexagonal.Domain.Seller.Ports.In.GetSellerApplicationsUseCase;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class GetSellerApplicationsUseCaseImpl implements GetSellerApplicationsUseCase {

    private final SellerProfileRepositoryPort sellerProfileRepository;

    @Override
    public List<SellerProfile> getPendingApplications() {
        return sellerProfileRepository.findByStatus(SellerStatus.PENDING_APPROVAL);
    }

    @Override
    public List<SellerProfile> getAllSellers() {
        return sellerProfileRepository.findAll();
    }
}
