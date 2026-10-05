package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Seller.Services.SellerService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.User.Exceptions.UserNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Infrastructura.DTO.Seller.SellerProfileResponseDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Seller.SellerReviewRequestDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/sellers")
@AllArgsConstructor
public class AdminSellerController {

    private final SellerService sellerService;
    private final UserService userService;

    @GetMapping("/pending")
    public ResponseEntity<List<SellerProfileResponseDTO>> getPendingApplications() {
        List<SellerProfileResponseDTO> list = sellerService.getPendingApplications().stream()
                .map(SellerProfileResponseDTO::from)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping
    public ResponseEntity<List<SellerProfileResponseDTO>> getAllSellers() {
        List<SellerProfileResponseDTO> list = sellerService.getAllSellers().stream()
                .map(SellerProfileResponseDTO::from)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SellerProfileResponseDTO> getById(@PathVariable String id) {
        SellerProfile profile = sellerService.getById(SellerProfileId.of(id));
        return ResponseEntity.ok(SellerProfileResponseDTO.from(profile));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<SellerProfileResponseDTO> approve(
            Authentication authentication,
            @PathVariable String id) {
        UserId adminId = resolveUserId(authentication);
        SellerProfile profile = sellerService.approve(SellerProfileId.of(id), adminId);
        return ResponseEntity.ok(SellerProfileResponseDTO.from(profile));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<SellerProfileResponseDTO> reject(
            Authentication authentication,
            @PathVariable String id,
            @Valid @RequestBody SellerReviewRequestDTO body) {
        UserId adminId = resolveUserId(authentication);
        SellerProfile profile = sellerService.reject(
                SellerProfileId.of(id), adminId, body.reason());
        return ResponseEntity.ok(SellerProfileResponseDTO.from(profile));
    }

    @PostMapping("/{id}/suspend")
    public ResponseEntity<SellerProfileResponseDTO> suspend(
            Authentication authentication,
            @PathVariable String id,
            @Valid @RequestBody SellerReviewRequestDTO body) {
        UserId adminId = resolveUserId(authentication);
        SellerProfile profile = sellerService.suspend(
                SellerProfileId.of(id), adminId, body.reason());
        return ResponseEntity.ok(SellerProfileResponseDTO.from(profile));
    }

    @PostMapping("/{id}/reactivate")
    public ResponseEntity<SellerProfileResponseDTO> reactivate(
            Authentication authentication,
            @PathVariable String id) {
        UserId adminId = resolveUserId(authentication);
        SellerProfile profile = sellerService.reactivate(SellerProfileId.of(id), adminId);
        return ResponseEntity.ok(SellerProfileResponseDTO.from(profile));
    }

    @PostMapping("/{id}/ban")
    public ResponseEntity<SellerProfileResponseDTO> ban(
            Authentication authentication,
            @PathVariable String id,
            @Valid @RequestBody SellerReviewRequestDTO body) {
        UserId adminId = resolveUserId(authentication);
        SellerProfile profile = sellerService.ban(SellerProfileId.of(id), adminId, body.reason());
        return ResponseEntity.ok(SellerProfileResponseDTO.from(profile));
    }

    private UserId resolveUserId(Authentication authentication) {
        String email = authentication.getName();
        return userService.findByEmail(email)
                .map(UserModel::getUserId)
                .orElseThrow(() -> new UserNotFoundException("Authenticated admin not found."));
    }
}
