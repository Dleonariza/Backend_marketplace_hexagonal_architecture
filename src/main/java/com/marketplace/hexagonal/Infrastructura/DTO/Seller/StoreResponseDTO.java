package com.marketplace.hexagonal.Infrastructura.DTO.Seller;

import com.marketplace.hexagonal.Domain.Product.Models.PageResult;
import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Infrastructura.DTO.Product.PagedProductResponseDTO;

public record StoreResponseDTO(
        String sellerId,
        String storeName,
        String storeDescription,
        String mainCategory,
        String logoUrl,
        String contactEmail,
        PagedProductResponseDTO products
) {
    public static StoreResponseDTO from(SellerProfile seller, PageResult<Product> products) {
        return new StoreResponseDTO(
                seller.getId().getValue(),
                seller.getStoreName(),
                seller.getStoreDescription(),
                seller.getMainCategory(),
                seller.getLogoUrl(),
                seller.getContactEmail(),
                PagedProductResponseDTO.from(products)
        );
    }
}
