package com.marketplace.hexagonal.Infrastructura.DTO.Seller;

import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;

import java.time.Instant;

public record SellerProfileResponseDTO(
        String id,
        String userId,
        String storeName,
        String storeDescription,
        String mainCategory,
        String contactEmail,
        String contactPhone,
        String logoUrl,
        String documentType,
        String documentNumber,
        String bankName,
        String accountNumber,
        String accountType,
        String accountHolderName,
        String status,
        String rejectionReason,
        String suspensionReason,
        Instant appliedAt,
        Instant reviewedAt
) {
    public static SellerProfileResponseDTO from(SellerProfile p) {
        return new SellerProfileResponseDTO(
                p.getId().getValue(),
                p.getUserId().getValue(),
                p.getStoreName(),
                p.getStoreDescription(),
                p.getMainCategory(),
                p.getContactEmail(),
                p.getContactPhone(),
                p.getLogoUrl(),
                p.getIdentityDocument().documentType().name(),
                p.getIdentityDocument().documentNumber(),
                p.getBankingInfo().bankName(),
                p.getBankingInfo().accountNumber(),
                p.getBankingInfo().accountType().name(),
                p.getBankingInfo().accountHolderName(),
                p.getStatus().name(),
                p.getRejectionReason(),
                p.getSuspensionReason(),
                p.getAppliedAt(),
                p.getReviewedAt()
        );
    }
}
