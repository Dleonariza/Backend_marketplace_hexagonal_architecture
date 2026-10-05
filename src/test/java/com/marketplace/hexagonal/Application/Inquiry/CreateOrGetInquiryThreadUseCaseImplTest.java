package com.marketplace.hexagonal.Application.Inquiry;

import com.marketplace.hexagonal.Application.Inquiry.UseCases.CreateOrGetInquiryThreadUseCaseImpl;
import com.marketplace.hexagonal.Domain.Inquiry.Exceptions.InquiryThreadNotAvailableException;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThread;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryThreadRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductStatus;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrGetInquiryThreadUseCaseImplTest {

    @Mock InquiryThreadRepositoryPort threadRepo;
    @Mock ProductRepositoryPort productRepo;
    @Mock SellerProfileRepositoryPort sellerProfileRepo;
    CreateOrGetInquiryThreadUseCaseImpl useCase;

    private static final UserId BUYER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final UserId SELLER_UID = UserId.of("bc66d237-f54d-4bad-b521-0eba3b06304c");
    private static final ProductId PRODUCT_ID = ProductId.of("aa000000-0000-0000-0000-000000000001");
    private static final SellerProfileId SELLER_ID = SellerProfileId.of("cc000000-0000-0000-0000-000000000001");

    @BeforeEach
    void setUp() {
        useCase = new CreateOrGetInquiryThreadUseCaseImpl(threadRepo, productRepo, sellerProfileRepo);
    }

    @Test
    void execute_noExistingThread_createsAndSavesNewThread() {
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.of(buildProduct()));
        when(sellerProfileRepo.findById(SELLER_ID)).thenReturn(Optional.of(buildSellerProfile()));
        when(threadRepo.findByBuyerIdAndProductId(BUYER_ID, PRODUCT_ID)).thenReturn(Optional.empty());
        when(threadRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InquiryThread result = useCase.execute(BUYER_ID, PRODUCT_ID.getValue());

        assertThat(result.getBuyerId()).isEqualTo(BUYER_ID);
        assertThat(result.getSellerUserId()).isEqualTo(SELLER_UID);
        assertThat(result.getProductId()).isEqualTo(PRODUCT_ID);
        verify(threadRepo).save(any());
    }

    @Test
    void execute_existingThread_returnsExistingWithoutSaving() {
        InquiryThread existing = InquiryThread.create(PRODUCT_ID, BUYER_ID, SELLER_UID);
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.of(buildProduct()));
        when(sellerProfileRepo.findById(SELLER_ID)).thenReturn(Optional.of(buildSellerProfile()));
        when(threadRepo.findByBuyerIdAndProductId(BUYER_ID, PRODUCT_ID)).thenReturn(Optional.of(existing));

        InquiryThread result = useCase.execute(BUYER_ID, PRODUCT_ID.getValue());

        assertThat(result).isEqualTo(existing);
        verify(threadRepo, never()).save(any());
    }

    @Test
    void execute_productNotFound_throwsInquiryThreadNotAvailableException() {
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(BUYER_ID, PRODUCT_ID.getValue()))
                .isInstanceOf(InquiryThreadNotAvailableException.class);
    }

    @Test
    void execute_deletedProduct_throwsInquiryThreadNotAvailableException() {
        Product deleted = buildProduct();
        deleted.setStatus(ProductStatus.DELETED);
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.of(deleted));

        assertThatThrownBy(() -> useCase.execute(BUYER_ID, PRODUCT_ID.getValue()))
                .isInstanceOf(InquiryThreadNotAvailableException.class);
    }

    private Product buildProduct() {
        return Product.builder()
                .id(PRODUCT_ID)
                .sellerId(SELLER_ID)
                .status(ProductStatus.ACTIVE)
                .build();
    }

    private SellerProfile buildSellerProfile() {
        return SellerProfile.builder()
                .id(SELLER_ID)
                .userId(SELLER_UID)
                .build();
    }
}
