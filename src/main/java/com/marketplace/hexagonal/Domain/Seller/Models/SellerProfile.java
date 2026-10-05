package com.marketplace.hexagonal.Domain.Seller.Models;

import com.marketplace.hexagonal.Domain.Seller.Exceptions.InvalidSellerStatusTransitionException;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SellerProfile {

    private SellerProfileId id;
    private UserId userId;

    private String storeName;
    private String storeDescription;
    private String mainCategory;
    private String contactEmail;
    private String contactPhone;
    private String logoUrl;

    private IdentityDocument identityDocument;
    private BankingInfo bankingInfo;

    private SellerStatus status;
    private String rejectionReason;
    private String suspensionReason;
    private Instant appliedAt;
    private Instant reviewedAt;
    private UserId reviewedBy;

    public static SellerProfile create(
            UserId userId,
            String storeName,
            String storeDescription,
            String mainCategory,
            String contactEmail,
            String contactPhone,
            String logoUrl,
            IdentityDocument identityDocument,
            BankingInfo bankingInfo) {
        return SellerProfile.builder()
                .id(SellerProfileId.generate())
                .userId(userId)
                .storeName(storeName)
                .storeDescription(storeDescription)
                .mainCategory(mainCategory)
                .contactEmail(contactEmail)
                .contactPhone(contactPhone)
                .logoUrl(logoUrl)
                .identityDocument(identityDocument)
                .bankingInfo(bankingInfo)
                .status(SellerStatus.PENDING_APPROVAL)
                .appliedAt(Instant.now())
                .build();
    }

    public static SellerProfile reconstitute(
            SellerProfileId id,
            UserId userId,
            String storeName,
            String storeDescription,
            String mainCategory,
            String contactEmail,
            String contactPhone,
            String logoUrl,
            IdentityDocument identityDocument,
            BankingInfo bankingInfo,
            SellerStatus status,
            String rejectionReason,
            String suspensionReason,
            Instant appliedAt,
            Instant reviewedAt,
            UserId reviewedBy) {
        SellerProfile p = new SellerProfile();
        p.id = id;
        p.userId = userId;
        p.storeName = storeName;
        p.storeDescription = storeDescription;
        p.mainCategory = mainCategory;
        p.contactEmail = contactEmail;
        p.contactPhone = contactPhone;
        p.logoUrl = logoUrl;
        p.identityDocument = identityDocument;
        p.bankingInfo = bankingInfo;
        p.status = status;
        p.rejectionReason = rejectionReason;
        p.suspensionReason = suspensionReason;
        p.appliedAt = appliedAt;
        p.reviewedAt = reviewedAt;
        p.reviewedBy = reviewedBy;
        return p;
    }

    public void approve(UserId adminId) {
        if (this.status != SellerStatus.PENDING_APPROVAL)
            throw new InvalidSellerStatusTransitionException(this.status, SellerStatus.ACTIVE);

        this.status = SellerStatus.ACTIVE;
        this.reviewedAt = Instant.now();
        this.reviewedBy = adminId;
        this.rejectionReason = null;
    }

    public void reject(UserId adminId, String reason) {
        if (this.status != SellerStatus.PENDING_APPROVAL)
            throw new InvalidSellerStatusTransitionException(this.status, SellerStatus.REJECTED);

        this.status = SellerStatus.REJECTED;
        this.reviewedAt = Instant.now();
        this.reviewedBy = adminId;
        this.rejectionReason = reason;
    }

    public void suspend(UserId adminId, String reason) {
        if (this.status != SellerStatus.ACTIVE)
            throw new InvalidSellerStatusTransitionException(this.status, SellerStatus.SUSPENDED);

        this.status = SellerStatus.SUSPENDED;
        this.reviewedAt = Instant.now();
        this.reviewedBy = adminId;
        this.suspensionReason = reason;
    }

    public void reactivate(UserId adminId) {
        if (this.status != SellerStatus.SUSPENDED)
            throw new InvalidSellerStatusTransitionException(this.status, SellerStatus.ACTIVE);

        this.status = SellerStatus.ACTIVE;
        this.reviewedAt = Instant.now();
        this.reviewedBy = adminId;
        this.suspensionReason = null;
    }

    public void cancel() {
        if (this.status != SellerStatus.PENDING_APPROVAL)
            throw new InvalidSellerStatusTransitionException(this.status, SellerStatus.CANCELLED);

        this.status = SellerStatus.CANCELLED;
    }

    public void ban(UserId adminId, String reason) {
        if (this.status == SellerStatus.BANNED)
            throw new InvalidSellerStatusTransitionException(this.status, SellerStatus.BANNED);

        this.status = SellerStatus.BANNED;
        this.reviewedAt = Instant.now();
        this.reviewedBy = adminId;
        this.suspensionReason = reason;
    }

    public void updateStoreInfo(
            String storeName,
            String storeDescription,
            String mainCategory,
            String contactEmail,
            String contactPhone,
            String logoUrl) {
        if (this.status != SellerStatus.ACTIVE && this.status != SellerStatus.SUSPENDED)
            throw new InvalidSellerStatusTransitionException(
                    "Only ACTIVE or SUSPENDED sellers can update their store profile. " +
                    "Current status: " + this.status);

        this.storeName = storeName;
        this.storeDescription = storeDescription;
        this.mainCategory = mainCategory;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.logoUrl = logoUrl;
    }

    public void reapply(
            String storeName,
            String storeDescription,
            String mainCategory,
            String contactEmail,
            String contactPhone,
            String logoUrl,
            IdentityDocument identityDocument,
            BankingInfo bankingInfo) {
        if (this.status != SellerStatus.REJECTED && this.status != SellerStatus.CANCELLED)
            throw new InvalidSellerStatusTransitionException(this.status, SellerStatus.PENDING_APPROVAL);

        this.storeName = storeName;
        this.storeDescription = storeDescription;
        this.mainCategory = mainCategory;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.logoUrl = logoUrl;
        this.identityDocument = identityDocument;
        this.bankingInfo = bankingInfo;
        this.status = SellerStatus.PENDING_APPROVAL;
        this.rejectionReason = null;
        this.appliedAt = Instant.now();
        this.reviewedAt = null;
        this.reviewedBy = null;
    }
}
