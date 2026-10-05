package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Cart.Services.CartService;
import com.marketplace.hexagonal.Application.Coupon.Services.CouponService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Cart.Models.PriceChangeInfo;
import com.marketplace.hexagonal.Domain.User.Exceptions.UserNotFoundException;

import java.util.List;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.DTO.Cart.AddToCartRequestDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Cart.CartResponseDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Cart.UpdateQuantityRequestDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Coupon.ApplyCouponRequestDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cart")
@AllArgsConstructor
public class CartController {

    private static final String SESSION_COOKIE = "CART_SESSION";
    private static final int COOKIE_MAX_AGE = 7 * 24 * 3600;

    private final CartService cartService;
    private final CouponService couponService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response) {
        Cart cart = isAuthenticated(authentication)
                ? cartService.getOrCreateCart(extractUserId(authentication))
                : cartService.getOrCreateAnonymousCart(resolveSessionId(request, response));

        List<PriceChangeInfo> priceChanges = cartService.checkPriceChanges(cart);
        return ResponseEntity.ok(CartResponseDTO.from(cart, priceChanges));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponseDTO> addItem(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response,
            @Valid @RequestBody AddToCartRequestDTO body) {
        Cart cart = isAuthenticated(authentication)
                ? cartService.addToCart(extractUserId(authentication), null,
                        body.productId(), body.variantId(), body.quantity())
                : cartService.addToCart(null, resolveSessionId(request, response),
                        body.productId(), body.variantId(), body.quantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(CartResponseDTO.from(cart));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponseDTO> updateQuantity(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String itemId,
            @Valid @RequestBody UpdateQuantityRequestDTO body) {
        Cart cart = isAuthenticated(authentication)
                ? cartService.updateQuantity(extractUserId(authentication), null, itemId, body.quantity())
                : cartService.updateQuantity(null, resolveSessionId(request, response), itemId, body.quantity());
        return ResponseEntity.ok(CartResponseDTO.from(cart));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponseDTO> removeItem(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String itemId) {
        Cart cart = isAuthenticated(authentication)
                ? cartService.removeFromCart(extractUserId(authentication), null, itemId)
                : cartService.removeFromCart(null, resolveSessionId(request, response), itemId);
        return ResponseEntity.ok(CartResponseDTO.from(cart));
    }

    @DeleteMapping
    public ResponseEntity<CartResponseDTO> clearCart(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response) {
        Cart cart = isAuthenticated(authentication)
                ? cartService.clearCart(extractUserId(authentication), null)
                : cartService.clearCart(null, resolveSessionId(request, response));
        return ResponseEntity.ok(CartResponseDTO.from(cart));
    }

    @PostMapping("/merge")
    public ResponseEntity<CartResponseDTO> mergeCarts(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response) {
        UserId userId = extractUserId(authentication);
        String sessionId = resolveSessionId(request, response);
        Cart cart = cartService.mergeCarts(userId, sessionId);
        clearSessionCookie(response);
        return ResponseEntity.ok(CartResponseDTO.from(cart));
    }

    @PostMapping("/coupon")
    public ResponseEntity<CartResponseDTO> applyCoupon(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response,
            @Valid @RequestBody ApplyCouponRequestDTO body) {
        Cart cart = isAuthenticated(authentication)
                ? couponService.applyCoupon(extractUserId(authentication), null, body.couponCode())
                : couponService.applyCoupon(null, resolveSessionId(request, response), body.couponCode());
        return ResponseEntity.ok(CartResponseDTO.from(cart));
    }

    @DeleteMapping("/coupon")
    public ResponseEntity<CartResponseDTO> removeCoupon(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response) {
        Cart cart = isAuthenticated(authentication)
                ? couponService.removeCoupon(extractUserId(authentication), null)
                : couponService.removeCoupon(null, resolveSessionId(request, response));
        return ResponseEntity.ok(CartResponseDTO.from(cart));
    }

    private boolean isAuthenticated(Authentication auth) {
        return auth != null && auth.isAuthenticated()
                && !(auth.getPrincipal() instanceof String s && s.equals("anonymousUser"));
    }

    private UserId extractUserId(Authentication authentication) {
        String email = authentication.getName();
        return userService.findByEmail(email)
                .map(u -> u.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found."));
    }

    private String resolveSessionId(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (SESSION_COOKIE.equals(c.getName())) return c.getValue();
            }
        }
        String newId = UUID.randomUUID().toString();
        Cookie cookie = new Cookie(SESSION_COOKIE, newId);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        response.addCookie(cookie);
        return newId;
    }

    private void clearSessionCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(SESSION_COOKIE, "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
