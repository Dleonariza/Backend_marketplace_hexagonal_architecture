package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Seller.Services.SellerService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerProfileNotFoundException;
import com.marketplace.hexagonal.Domain.Seller.Models.*;
import com.marketplace.hexagonal.Domain.User.Exceptions.UserNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Infrastructura.DTO.Seller.SellerApplicationRequestDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Seller.SellerDashboardDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Seller.SellerProfileResponseDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Seller.UpdateSellerProfileRequestDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller")
@AllArgsConstructor
public class SellerController {

    private final SellerService sellerService;
    private final UserService userService;

    @PostMapping("/apply")
    public ResponseEntity<SellerProfileResponseDTO> apply(
            Authentication authentication,
            @Valid @RequestBody SellerApplicationRequestDTO request) {
        UserId userId = resolveUserId(authentication);

        SellerProfile saved = sellerService.apply(
                userId,
                request.storeName(),
                request.storeDescription(),
                request.mainCategory(),
                request.contactEmail(),
                request.contactPhone(),
                request.logoUrl(),
                new IdentityDocument(request.documentType(), request.documentNumber()),
                new BankingInfo(request.bankName(), request.accountNumber(), request.accountType(),
                        request.accountHolderName(), request.accountHolderDocument()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SellerProfileResponseDTO.from(saved));
    }

    @GetMapping("/me")
    public ResponseEntity<SellerProfileResponseDTO> getMyProfile(Authentication authentication) {
        UserId userId = resolveUserId(authentication);
        return sellerService.findByUserId(userId)
                .map(p -> ResponseEntity.ok(SellerProfileResponseDTO.from(p)))
                .orElseThrow(() -> new SellerProfileNotFoundException(
                        "No seller profile found for the current user."));
    }

    @DeleteMapping("/application")
    public ResponseEntity<Void> cancelApplication(Authentication authentication) {
        UserId userId = resolveUserId(authentication);
        sellerService.cancelApplication(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<SellerDashboardDTO> getDashboard(Authentication authentication) {
        UserId userId = resolveUserId(authentication);
        SellerProfile profile = sellerService.findByUserId(userId)
                .orElseThrow(() -> new SellerProfileNotFoundException(
                        "No seller profile found for the current user."));
        return ResponseEntity.ok(SellerDashboardDTO.from(profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<SellerProfileResponseDTO> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateSellerProfileRequestDTO request) {
        UserId userId = resolveUserId(authentication);
        SellerProfile updated = sellerService.updateProfile(
                userId,
                request.storeName(),
                request.storeDescription(),
                request.mainCategory(),
                request.contactEmail(),
                request.contactPhone(),
                request.logoUrl());
        return ResponseEntity.ok(SellerProfileResponseDTO.from(updated));
    }

    private UserId resolveUserId(Authentication authentication) {
        String email = authentication.getName();
        return userService.findByEmail(email)
                .map(UserModel::getUserId)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found."));
    }
}
