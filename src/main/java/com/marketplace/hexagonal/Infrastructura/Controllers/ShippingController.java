package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Shipping.Services.ShippingService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Shipping.Models.ShippingQuote;
import com.marketplace.hexagonal.Domain.User.Exceptions.UserNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.DTO.Shipping.ShippingQuoteResponseDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/checkout")
@AllArgsConstructor
public class ShippingController {

    private static final String SESSION_COOKIE = "CART_SESSION";

    private final ShippingService shippingService;
    private final UserService userService;

    @GetMapping("/shipping")
    public ResponseEntity<List<ShippingQuoteResponseDTO>> getShippingOptions(
            Authentication authentication,
            HttpServletRequest request) {
        UserId userId = isAuthenticated(authentication) ? extractUserId(authentication) : null;
        String sessionId = userId == null ? extractSessionId(request) : null;

        List<ShippingQuote> quotes = shippingService.calculateShipping(userId, sessionId);

        return ResponseEntity.ok(
                quotes.stream().map(ShippingQuoteResponseDTO::from).toList());
    }

    private boolean isAuthenticated(Authentication auth) {
        return auth != null && auth.isAuthenticated()
                && !(auth.getPrincipal() instanceof String s && s.equals("anonymousUser"));
    }

    private UserId extractUserId(Authentication auth) {
        return userService.findByEmail(auth.getName())
                .map(u -> u.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found."));
    }

    private String extractSessionId(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (SESSION_COOKIE.equals(c.getName())) return c.getValue();
            }
        }
        return null;
    }
}
