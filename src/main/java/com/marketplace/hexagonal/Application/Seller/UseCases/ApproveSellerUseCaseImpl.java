package com.marketplace.hexagonal.Application.Seller.UseCases;

import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerProfileNotFoundException;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Seller.Ports.In.ApproveSellerUseCase;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.RoleAndPermission.RoleEnum;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApproveSellerUseCaseImpl implements ApproveSellerUseCase {

    private final SellerProfileRepositoryPort sellerProfileRepository;
    private final UserRepositoryPort userRepository;

    @Override
    public SellerProfile approve(SellerProfileId id, UserId adminId) {
        SellerProfile profile = sellerProfileRepository.findById(id)
                .orElseThrow(() -> new SellerProfileNotFoundException(id));

        profile.approve(adminId);
        SellerProfile saved = sellerProfileRepository.save(profile);

        userRepository.addRole(profile.getUserId(), RoleEnum.SELLER);

        return saved;
    }
}
