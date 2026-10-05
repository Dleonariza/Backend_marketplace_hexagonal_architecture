package com.marketplace.hexagonal.Domain.Seller.Ports.In;

import com.marketplace.hexagonal.Domain.Seller.Models.BankingInfo;
import com.marketplace.hexagonal.Domain.Seller.Models.IdentityDocument;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public interface ApplySellerUseCase {

    SellerProfile apply(UserId userId,
                        String storeName,
                        String storeDescription,
                        String mainCategory,
                        String contactEmail,
                        String contactPhone,
                        String logoUrl,
                        IdentityDocument identityDocument,
                        BankingInfo bankingInfo);
}
