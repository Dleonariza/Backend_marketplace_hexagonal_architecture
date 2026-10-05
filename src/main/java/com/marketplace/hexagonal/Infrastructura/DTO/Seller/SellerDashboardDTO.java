package com.marketplace.hexagonal.Infrastructura.DTO.Seller;

import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerStatus;

import java.time.Instant;

public record SellerDashboardDTO(

        String id,

        StoreSection store,

        StatusSection status,

        DocumentSection document,

        BankingSection banking,

        TimelineSection timeline,

        StatsSection stats
) {
    public record StoreSection(
            String storeName,
            String storeDescription,
            String mainCategory,
            String contactEmail,
            String contactPhone,
            String logoUrl
    ) {}

    public record StatusSection(
            String code,
            String description,
            String rejectionReason,
            String suspensionReason
    ) {}

    public record DocumentSection(
            String documentType,
            String maskedDocumentNumber
    ) {}

    public record BankingSection(
            String bankName,
            String accountType,
            String maskedAccountNumber,
            String accountHolderName
    ) {}

    public record TimelineSection(
            Instant appliedAt,
            Instant reviewedAt
    ) {}

    public record StatsSection(
            int products,
            int activeOrders
    ) {}

    public static SellerDashboardDTO from(SellerProfile p) {
        StoreSection store = new StoreSection(
                p.getStoreName(),
                p.getStoreDescription(),
                p.getMainCategory(),
                p.getContactEmail(),
                p.getContactPhone(),
                p.getLogoUrl()
        );

        StatusSection status = new StatusSection(
                p.getStatus().name(),
                statusDescription(p.getStatus()),
                p.getRejectionReason(),
                p.getSuspensionReason()
        );

        DocumentSection document = new DocumentSection(
                p.getIdentityDocument() != null
                        ? p.getIdentityDocument().documentType().name()
                        : null,
                p.getIdentityDocument() != null
                        ? maskDocNumber(p.getIdentityDocument().documentNumber())
                        : null
        );

        BankingSection banking = new BankingSection(
                p.getBankingInfo() != null ? p.getBankingInfo().bankName() : null,
                p.getBankingInfo() != null ? p.getBankingInfo().accountType().name() : null,
                p.getBankingInfo() != null ? maskAccount(p.getBankingInfo().accountNumber()) : null,
                p.getBankingInfo() != null ? p.getBankingInfo().accountHolderName() : null
        );

        TimelineSection timeline = new TimelineSection(
                p.getAppliedAt(),
                p.getReviewedAt()
        );

        StatsSection stats = new StatsSection(0, 0);

        return new SellerDashboardDTO(
                p.getId().getValue(),
                store, status, document, banking, timeline, stats
        );
    }

    private static String statusDescription(SellerStatus s) {
        return switch (s) {
            case PENDING_APPROVAL -> "Your application is under review by an administrator.";
            case ACTIVE -> "Your store is active and can receive orders.";
            case SUSPENDED -> "Your account has been temporarily suspended. Please contact support.";
            case REJECTED -> "Your application was rejected. You may correct your information and reapply.";
            case CANCELLED -> "You cancelled your application. You may reapply at any time.";
            case BANNED -> "Your account has been permanently banned.";
        };
    }

    private static String maskDocNumber(String doc) {
        if (doc == null || doc.length() <= 4) return doc;
        return "****" + doc.substring(doc.length() - 4);
    }

    private static String maskAccount(String account) {
        if (account == null || account.length() <= 4) return account;
        return "****" + account.substring(account.length() - 4);
    }
}
