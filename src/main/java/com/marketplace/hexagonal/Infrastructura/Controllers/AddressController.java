package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.User.Services.AddressService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.User.Exceptions.UserNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.UserAddress;
import com.marketplace.hexagonal.Domain.User.Models.UserAddressId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.DTO.Address.AddressRequestDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Address.AddressResponseDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addresses")
@AllArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> getAddresses(Authentication authentication) {
        UserId userId = resolveUserId(authentication);
        List<AddressResponseDTO> response = addressService.getAddresses(userId)
                .stream()
                .map(AddressResponseDTO::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AddressResponseDTO> addAddress(
            Authentication authentication,
            @Valid @RequestBody AddressRequestDTO request) {
        UserId userId = resolveUserId(authentication);
        UserAddress address = toDomain(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AddressResponseDTO.from(addressService.addAddress(userId, address)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> updateAddress(
            Authentication authentication,
            @PathVariable String id,
            @RequestBody AddressRequestDTO request) {
        UserId userId = resolveUserId(authentication);
        UserAddress updated = toDomain(request);
        return ResponseEntity.ok(AddressResponseDTO.from(
                addressService.updateAddress(userId, UserAddressId.of(id), updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(
            Authentication authentication,
            @PathVariable String id) {
        UserId userId = resolveUserId(authentication);
        addressService.deleteAddress(userId, UserAddressId.of(id));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/default")
    public ResponseEntity<AddressResponseDTO> setDefault(
            Authentication authentication,
            @PathVariable String id) {
        UserId userId = resolveUserId(authentication);
        return ResponseEntity.ok(AddressResponseDTO.from(
                addressService.setDefault(userId, UserAddressId.of(id))));
    }

    private UserId resolveUserId(Authentication authentication) {
        String email = authentication.getName();
        return userService.findByEmail(email)
                .map(u -> u.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found."));
    }

    private UserAddress toDomain(AddressRequestDTO request) {
        return UserAddress.builder()
                .street(request.street())
                .city(request.city())
                .state(request.state())
                .postalCode(request.postalCode())
                .country(request.country())
                .isDefault(request.isDefault())
                .build();
    }
}
