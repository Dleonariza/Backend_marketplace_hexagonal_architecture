package com.marketplace.hexagonal.Infrastructura.DTO.Seller;

import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;

public record StoreSummaryResponseDTO(
        String sellerId,
        String storeName,
        String storeDescription,
        String mainCategory,
        String logoUrl
) {
    public static StoreSummaryResponseDTO from(SellerProfile seller) {
        return new StoreSummaryResponseDTO(
                seller.getId().getValue(),
                seller.getStoreName(),
                seller.getStoreDescription(),
                seller.getMainCategory(),
                seller.getLogoUrl()
        );
    }
}
