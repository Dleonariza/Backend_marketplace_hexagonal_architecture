package com.marketplace.hexagonal.Application.Inquiry.UseCases;

import com.marketplace.hexagonal.Domain.Inquiry.Exceptions.InquiryThreadNotAvailableException;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThread;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.In.CreateOrGetInquiryThreadUseCase;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryThreadRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductStatus;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateOrGetInquiryThreadUseCaseImpl implements CreateOrGetInquiryThreadUseCase {

    private final InquiryThreadRepositoryPort inquiryThreadRepo;
    private final ProductRepositoryPort productRepo;
    private final SellerProfileRepositoryPort sellerProfileRepo;

    @Override
    public InquiryThread execute(UserId buyerId, String productIdValue) {
        ProductId productId = ProductId.of(productIdValue);

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new InquiryThreadNotAvailableException(
                        "Product not found: " + productIdValue));

        if (product.getStatus() == ProductStatus.DELETED)
            throw new InquiryThreadNotAvailableException(
                    "Cannot start an inquiry on a deleted product.");

        SellerProfile sellerProfile = sellerProfileRepo.findById(product.getSellerId())
                .orElseThrow(() -> new InquiryThreadNotAvailableException(
                        "Seller profile not found for product: " + productIdValue));

        UserId sellerUserId = sellerProfile.getUserId();

        return inquiryThreadRepo.findByBuyerIdAndProductId(buyerId, productId)
                .orElseGet(() -> inquiryThreadRepo.save(
                        InquiryThread.create(productId, buyerId, sellerUserId)));
    }
}
