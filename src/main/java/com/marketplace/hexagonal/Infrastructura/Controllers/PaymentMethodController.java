package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.User.Services.PaymentMethodService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.User.Exceptions.UserNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.PaymentMethod;
import com.marketplace.hexagonal.Domain.User.Models.PaymentMethodId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.DTO.PaymentMethod.AddPaymentMethodRequestDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.PaymentMethod.PaymentMethodResponseDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/payment-methods")
@AllArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<PaymentMethodResponseDTO>> list(Authentication authentication) {
        UserId userId = resolveUserId(authentication);
        List<PaymentMethodResponseDTO> response = paymentMethodService
                .listPaymentMethods(userId)
                .stream()
                .map(PaymentMethodResponseDTO::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PaymentMethodResponseDTO> add(
            Authentication authentication,
            @Valid @RequestBody AddPaymentMethodRequestDTO request) {
        UserId userId = resolveUserId(authentication);
        PaymentMethod paymentMethod = toDomain(request);
        PaymentMethod saved = paymentMethodService.addPaymentMethod(userId, paymentMethod);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PaymentMethodResponseDTO.from(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            Authentication authentication,
            @PathVariable String id) {
        UserId userId = resolveUserId(authentication);
        paymentMethodService.deletePaymentMethod(userId, PaymentMethodId.of(id));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/default")
    public ResponseEntity<PaymentMethodResponseDTO> setDefault(
            Authentication authentication,
            @PathVariable String id) {
        UserId userId = resolveUserId(authentication);
        PaymentMethod updated = paymentMethodService
                .setDefaultPaymentMethod(userId, PaymentMethodId.of(id));
        return ResponseEntity.ok(PaymentMethodResponseDTO.from(updated));
    }

    private UserId resolveUserId(Authentication authentication) {
        String email = authentication.getName();
        return userService.findByEmail(email)
                .map(u -> u.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found."));
    }

    private PaymentMethod toDomain(AddPaymentMethodRequestDTO request) {
        PaymentMethod pm = new PaymentMethod();
        pm.setStripePaymentMethodId(request.stripePaymentMethodId());
        pm.setLast4(request.last4());
        pm.setBrand(request.brand());
        pm.setExpMonth(request.expMonth());
        pm.setExpYear(request.expYear());
        pm.setCardholderName(request.cardholderName());
        pm.setDefault(Boolean.TRUE.equals(request.isDefault()));
        return pm;
    }
}
