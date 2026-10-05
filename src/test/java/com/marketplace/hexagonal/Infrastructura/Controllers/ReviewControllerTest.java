package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.hexagonal.Application.Review.Services.ReviewService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Review.Exceptions.CannotReviewOwnContentException;
import com.marketplace.hexagonal.Domain.Review.Exceptions.ReviewAlreadyExistsException;
import com.marketplace.hexagonal.Domain.Review.Exceptions.ReviewNotFoundException;
import com.marketplace.hexagonal.Domain.Review.Models.Review;
import com.marketplace.hexagonal.Domain.Review.Models.ReviewId;
import com.marketplace.hexagonal.Domain.Review.Models.ReviewTargetType;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.Exception.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock ReviewService reviewService;
    @Mock UserService userService;
    @InjectMocks ReviewController controller;
    MockMvc mockMvc;
    ObjectMapper mapper = new ObjectMapper();

    private static final String USER_EMAIL = "buyer@test.com";
    private static final UserId USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final String PRODUCT_ID = "aa000000-0000-0000-0000-000000000001";
    private static final String SELLER_ID = "bc66d237-f54d-4bad-b521-0eba3b06304c";
    private static final ReviewId REVIEW_ID = ReviewId.generate();

    @BeforeEach
    void setUp() {
        var auth = new UsernamePasswordAuthenticationToken(
                USER_EMAIL, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        UserModel user = UserModel.create(USER_EMAIL, "buyer1", "hash", "Juan", null, "3001234567", List.of());
        org.mockito.Mockito.lenient()
                .when(userService.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(user));

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .addFilter((req, res, chain) -> chain.doFilter(
                        new HttpServletRequestWrapper((HttpServletRequest) req) {
                            @Override public java.security.Principal getUserPrincipal() {
                                Authentication a = SecurityContextHolder.getContext().getAuthentication();
                                return a;
                            }
                        }, res))
                .build();
    }

    @AfterEach
    void tearDown() { SecurityContextHolder.clearContext(); }

    @Test
    void listProductReviews_returnsSummaryWithAverage() throws Exception {
        when(reviewService.listProductReviews(PRODUCT_ID))
                .thenReturn(List.of(buildReview(5), buildReview(3)));

        mockMvc.perform(get("/products/" + PRODUCT_ID + "/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReviews").value(2))
                .andExpect(jsonPath("$.averageRating").value(4.00))
                .andExpect(jsonPath("$.reviews.length()").value(2));
    }

    @Test
    void listProductReviews_empty_returnsZeroAverage() throws Exception {
        when(reviewService.listProductReviews(PRODUCT_ID)).thenReturn(List.of());

        mockMvc.perform(get("/products/" + PRODUCT_ID + "/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReviews").value(0))
                .andExpect(jsonPath("$.averageRating").value(0));
    }

    @Test
    void addProductReview_valid_returns201() throws Exception {
        Review review = buildReview(5);
        when(reviewService.addProductReview(any(), anyString(), anyString(), anyInt(), anyString()))
                .thenReturn(review);

        mockMvc.perform(post("/products/" + PRODUCT_ID + "/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\":5,\"comment\":\"Excellent!\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.authorName").value("Juan"));
    }

    @Test
    void addProductReview_duplicate_returns409() throws Exception {
        when(reviewService.addProductReview(any(), anyString(), anyString(), anyInt(), anyString()))
                .thenThrow(new ReviewAlreadyExistsException(USER_ID, ReviewTargetType.PRODUCT, PRODUCT_ID));

        mockMvc.perform(post("/products/" + PRODUCT_ID + "/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\":4,\"comment\":\"Again!\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void addProductReview_invalidRating_returns400() throws Exception {
        mockMvc.perform(post("/products/" + PRODUCT_ID + "/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\":6,\"comment\":\"Bad!\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listSellerReviews_returnsSummary() throws Exception {
        when(reviewService.listSellerReviews(SELLER_ID))
                .thenReturn(List.of(buildSellerReview(4)));

        mockMvc.perform(get("/sellers/" + SELLER_ID + "/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReviews").value(1))
                .andExpect(jsonPath("$.averageRating").value(4.00));
    }

    @Test
    void addSellerReview_selfReview_returns403() throws Exception {
        when(reviewService.addSellerReview(any(), anyString(), anyString(), anyInt(), anyString()))
                .thenThrow(new CannotReviewOwnContentException());

        mockMvc.perform(post("/sellers/" + SELLER_ID + "/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\":5,\"comment\":\"I love myself!\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteReview_asAuthor_returns204() throws Exception {
        mockMvc.perform(delete("/reviews/" + REVIEW_ID.getValue()))
                .andExpect(status().isNoContent());
        verify(reviewService).deleteReview(any(), any());
    }

    @Test
    void deleteReview_notFound_returns404() throws Exception {
        doThrow(new ReviewNotFoundException(REVIEW_ID))
                .when(reviewService).deleteReview(any(), any());

        mockMvc.perform(delete("/reviews/" + REVIEW_ID.getValue()))
                .andExpect(status().isNotFound());
    }

    private Review buildReview(int rating) {
        return Review.builder()
                .reviewId(REVIEW_ID)
                .authorId(USER_ID)
                .authorName("Juan")
                .targetType(ReviewTargetType.PRODUCT)
                .targetId(PRODUCT_ID)
                .rating(rating)
                .comment("Great!")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private Review buildSellerReview(int rating) {
        return Review.builder()
                .reviewId(REVIEW_ID)
                .authorId(USER_ID)
                .authorName("Juan")
                .targetType(ReviewTargetType.SELLER)
                .targetId(SELLER_ID)
                .rating(rating)
                .comment("Great seller!")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}
