package com.marketplace.hexagonal.Application.Seller.Services;

import com.marketplace.hexagonal.Domain.Seller.Events.SellerVerificationCompletedEvent;
import com.marketplace.hexagonal.Domain.Seller.Models.BankingInfo;
import com.marketplace.hexagonal.Domain.Seller.Models.IdentityDocument;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Seller.Ports.In.*;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class SellerService implements
        ApplySellerUseCase,
        ApproveSellerUseCase,
        RejectSellerUseCase,
        SuspendSellerUseCase,
        ReactivateSellerUseCase,
        CancelSellerApplicationUseCase,
        BanSellerUseCase,
        GetSellerProfileUseCase,
        GetSellerApplicationsUseCase,
        UpdateSellerProfileUseCase {
    private final ApplySellerUseCase applySellerUseCase;
    private final ApproveSellerUseCase approveSellerUseCase;
    private final RejectSellerUseCase rejectSellerUseCase;
    private final SuspendSellerUseCase suspendSellerUseCase;
    private final ReactivateSellerUseCase reactivateSellerUseCase;
    private final CancelSellerApplicationUseCase cancelSellerApplicationUseCase;
    private final BanSellerUseCase banSellerUseCase;
    private final GetSellerProfileUseCase getSellerProfileUseCase;
    private final GetSellerApplicationsUseCase getSellerApplicationsUseCase;
    private final UpdateSellerProfileUseCase updateSellerProfileUseCase;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public SellerProfile apply(UserId userId,
                               String storeName,
                               String storeDescription,
                               String mainCategory,
                               String contactEmail,
                               String contactPhone,
                               String logoUrl,
                               IdentityDocument identityDocument,
                               BankingInfo bankingInfo) {
        return applySellerUseCase.apply(userId, storeName, storeDescription, mainCategory,
                contactEmail, contactPhone, logoUrl, identityDocument, bankingInfo);
    }

    @Override
    public SellerProfile approve(SellerProfileId id, UserId adminId) {
        SellerProfile saved = approveSellerUseCase.approve(id, adminId);
        eventPublisher.publishEvent(new SellerVerificationCompletedEvent(
                saved.getUserId(), saved.getStoreName(), saved.getStatus(), null));
        return saved;
    }

    @Override
    public SellerProfile reject(SellerProfileId id, UserId adminId, String reason) {
        SellerProfile saved = rejectSellerUseCase.reject(id, adminId, reason);
        eventPublisher.publishEvent(new SellerVerificationCompletedEvent(
                saved.getUserId(), saved.getStoreName(), saved.getStatus(), saved.getRejectionReason()));
        return saved;
    }

    @Override
    public SellerProfile suspend(SellerProfileId id, UserId adminId, String reason) {
        return suspendSellerUseCase.suspend(id, adminId, reason);
    }

    @Override
    public SellerProfile reactivate(SellerProfileId id, UserId adminId) {
        return reactivateSellerUseCase.reactivate(id, adminId);
    }

    @Override
    public void cancelApplication(UserId userId) {
        cancelSellerApplicationUseCase.cancelApplication(userId);
    }

    @Override
    public SellerProfile ban(SellerProfileId id, UserId adminId, String reason) {
        return banSellerUseCase.ban(id, adminId, reason);
    }

    @Override
    public SellerProfile getById(SellerProfileId id) {
        return getSellerProfileUseCase.getById(id);
    }

    @Override
    public Optional<SellerProfile> findByUserId(UserId userId) {
        return getSellerProfileUseCase.findByUserId(userId);
    }

    @Override
    public List<SellerProfile> getPendingApplications() {
        return getSellerApplicationsUseCase.getPendingApplications();
    }

    @Override
    public List<SellerProfile> getAllSellers() {
        return getSellerApplicationsUseCase.getAllSellers();
    }

    @Override
    public SellerProfile updateProfile(UserId userId,
                                       String storeName,
                                       String storeDescription,
                                       String mainCategory,
                                       String contactEmail,
                                       String contactPhone,
                                       String logoUrl) {
        return updateSellerProfileUseCase.updateProfile(
                userId, storeName, storeDescription, mainCategory,
                contactEmail, contactPhone, logoUrl);
    }
}
