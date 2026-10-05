package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Review.Services.ReviewService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Review.Models.Review;
import com.marketplace.hexagonal.Domain.Review.Models.ReviewId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.DTO.Review.AddReviewRequestDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Review.ReviewResponseDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Review.ReviewSummaryResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<ReviewSummaryResponseDTO> listProductReviews(
            @PathVariable String productId) {
        List<Review> reviews = reviewService.listProductReviews(productId);
        return ResponseEntity.ok(ReviewSummaryResponseDTO.from(reviews));
    }

    @PostMapping("/products/{productId}/reviews")
    public ResponseEntity<ReviewResponseDTO> addProductReview(
            @PathVariable String productId,
            @Valid @RequestBody AddReviewRequestDTO dto,
            Principal principal) {
        UserId userId = resolveUserId(principal);
        String userName = resolveUserName(principal);

        Review review = reviewService.addProductReview(
                userId, userName, productId, dto.rating(), dto.comment());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReviewResponseDTO.from(review));
    }

    @GetMapping("/sellers/{sellerId}/reviews")
    public ResponseEntity<ReviewSummaryResponseDTO> listSellerReviews(
            @PathVariable String sellerId) {
        List<Review> reviews = reviewService.listSellerReviews(sellerId);
        return ResponseEntity.ok(ReviewSummaryResponseDTO.from(reviews));
    }

    @PostMapping("/sellers/{sellerId}/reviews")
    public ResponseEntity<ReviewResponseDTO> addSellerReview(
            @PathVariable String sellerId,
            @Valid @RequestBody AddReviewRequestDTO dto,
            Principal principal) {
        UserId userId = resolveUserId(principal);
        String userName = resolveUserName(principal);

        Review review = reviewService.addSellerReview(
                userId, userName, sellerId, dto.rating(), dto.comment());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReviewResponseDTO.from(review));
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable String reviewId,
            Principal principal) {
        UserId userId = resolveUserId(principal);
        reviewService.deleteReview(ReviewId.of(reviewId), userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/reviews/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> adminDeleteReview(@PathVariable String reviewId) {
        reviewService.adminDeleteReview(ReviewId.of(reviewId));
        return ResponseEntity.noContent().build();
    }

    private UserId resolveUserId(Principal principal) {
        return userService.findByEmail(principal.getName())
                .map(u -> u.getUserId())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }

    private String resolveUserName(Principal principal) {
        return userService.findByEmail(principal.getName())
                .map(u -> u.getName())
                .orElse(principal.getName());
    }
}
