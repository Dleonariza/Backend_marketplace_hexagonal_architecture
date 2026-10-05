package com.marketplace.hexagonal.Infrastructura.Mappers;

import com.marketplace.hexagonal.Domain.Seller.Models.*;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.Entities.Seller.SellerProfileEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.User.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SellerProfileMapper {

    default SellerProfileEntity toEntity(SellerProfile profile, UserEntity userEntity) {
        if (profile == null) return null;

        return SellerProfileEntity.builder()
                .id(UUID.fromString(profile.getId().getValue()))
                .user(userEntity)
                .storeName(profile.getStoreName())
                .storeDescription(profile.getStoreDescription())
                .mainCategory(profile.getMainCategory())
                .contactEmail(profile.getContactEmail())
                .contactPhone(profile.getContactPhone())
                .logoUrl(profile.getLogoUrl())
                .documentType(profile.getIdentityDocument().documentType())
                .documentNumber(profile.getIdentityDocument().documentNumber())
                .bankName(profile.getBankingInfo().bankName())
                .accountNumber(profile.getBankingInfo().accountNumber())
                .accountType(profile.getBankingInfo().accountType())
                .accountHolderName(profile.getBankingInfo().accountHolderName())
                .accountHolderDocument(profile.getBankingInfo().accountHolderDocument())
                .status(profile.getStatus())
                .rejectionReason(profile.getRejectionReason())
                .suspensionReason(profile.getSuspensionReason())
                .appliedAt(profile.getAppliedAt())
                .reviewedAt(profile.getReviewedAt())
                .reviewedBy(profile.getReviewedBy() != null
                        ? UUID.fromString(profile.getReviewedBy().getValue()) : null)
                .build();
    }

    default SellerProfile toDomain(SellerProfileEntity entity) {
        if (entity == null) return null;

        return SellerProfile.reconstitute(
                SellerProfileId.of(entity.getId().toString()),
                UserId.of(entity.getUser().getId().toString()),
                entity.getStoreName(),
                entity.getStoreDescription(),
                entity.getMainCategory(),
                entity.getContactEmail(),
                entity.getContactPhone(),
                entity.getLogoUrl(),
                new IdentityDocument(entity.getDocumentType(), entity.getDocumentNumber()),
                new BankingInfo(
                        entity.getBankName(),
                        entity.getAccountNumber(),
                        entity.getAccountType(),
                        entity.getAccountHolderName(),
                        entity.getAccountHolderDocument()),
                entity.getStatus(),
                entity.getRejectionReason(),
                entity.getSuspensionReason(),
                entity.getAppliedAt(),
                entity.getReviewedAt(),
                entity.getReviewedBy() != null
                        ? UserId.of(entity.getReviewedBy().toString()) : null
        );
    }
}
