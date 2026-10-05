package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Shipping.Services.ShippingService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerProfileNotFoundException;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.Shipping.Models.SellerShippingOption;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.DTO.Shipping.AddShippingOptionRequestDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Shipping.ShippingOptionResponseDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Shipping.UpdateShippingOptionRequestDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seller/shipping-options")
@AllArgsConstructor
public class SellerShippingController {

    private final ShippingService shippingService;
    private final UserService userService;
    private final SellerProfileRepositoryPort sellerRepo;

    @PostMapping
    public ResponseEntity<ShippingOptionResponseDTO> add(
            Authentication auth,
            @Valid @RequestBody AddShippingOptionRequestDTO body) {
        SellerProfileId sellerId = resolveSellerProfileId(auth);
        Money price = Money.of(body.price(), body.currency());

        SellerShippingOption option = shippingService.addShippingOption(
                sellerId, body.name(), body.description(), price,
                body.estimatedDaysMin(), body.estimatedDaysMax());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ShippingOptionResponseDTO.from(option));
    }

    @GetMapping
    public ResponseEntity<List<ShippingOptionResponseDTO>> list(Authentication auth) {
        SellerProfileId sellerId = resolveSellerProfileId(auth);
        List<ShippingOptionResponseDTO> list = shippingService.listShippingOptions(sellerId)
                .stream().map(ShippingOptionResponseDTO::from).toList();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShippingOptionResponseDTO> update(
            Authentication auth,
            @PathVariable String id,
            @Valid @RequestBody UpdateShippingOptionRequestDTO body) {
        SellerProfileId sellerId = resolveSellerProfileId(auth);
        Money price = Money.of(body.price(), body.currency());

        SellerShippingOption option = shippingService.updateShippingOption(
                sellerId, id, body.name(), body.description(), price,
                body.estimatedDaysMin(), body.estimatedDaysMax());

        return ResponseEntity.ok(ShippingOptionResponseDTO.from(option));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(Authentication auth, @PathVariable String id) {
        shippingService.removeShippingOption(resolveSellerProfileId(auth), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(Authentication auth, @PathVariable String id) {
        shippingService.activateShippingOption(resolveSellerProfileId(auth), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(Authentication auth, @PathVariable String id) {
        shippingService.deactivateShippingOption(resolveSellerProfileId(auth), id);
        return ResponseEntity.noContent().build();
    }

    private SellerProfileId resolveSellerProfileId(Authentication auth) {
        String email = auth.getName();
        UserId userId = userService.findByEmail(email)
                .map(u -> u.getUserId())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
        SellerProfile profile = sellerRepo.findByUserId(userId)
                .orElseThrow(() -> new SellerProfileNotFoundException(
                        "Seller profile not found for user: " + email));
        return profile.getId();
    }
}
