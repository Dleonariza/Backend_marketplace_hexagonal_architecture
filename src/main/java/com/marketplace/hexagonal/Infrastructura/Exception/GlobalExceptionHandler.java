package com.marketplace.hexagonal.Infrastructura.Exception;

import com.marketplace.hexagonal.Domain.Auth.Exceptions.ExpiredTokenException;
import com.marketplace.hexagonal.Domain.Auth.Exceptions.InvalidTokenException;
import com.marketplace.hexagonal.Domain.Cart.Exceptions.*;
import com.marketplace.hexagonal.Domain.Coupon.Exceptions.*;
import com.marketplace.hexagonal.Domain.Order.Exceptions.*;
import com.marketplace.hexagonal.Domain.Shipping.Exceptions.*;
import com.marketplace.hexagonal.Domain.Product.Exceptions.*;
import com.marketplace.hexagonal.Domain.Seller.Exceptions.InvalidSellerStatusTransitionException;
import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerAlreadyAppliedException;
import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerBannedException;
import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerProfileNotFoundException;
import com.marketplace.hexagonal.Domain.Favorite.Exceptions.FavoriteAlreadyExistsException;
import com.marketplace.hexagonal.Domain.Favorite.Exceptions.FavoriteNotFoundException;
import com.marketplace.hexagonal.Domain.Review.Exceptions.*;
import com.marketplace.hexagonal.Domain.Chat.Exceptions.ChatRoomAccessDeniedException;
import com.marketplace.hexagonal.Domain.Chat.Exceptions.ChatRoomNotFoundException;
import com.marketplace.hexagonal.Domain.Chat.Exceptions.ChatRoomNotAvailableException;
import com.marketplace.hexagonal.Domain.Inquiry.Exceptions.InquiryThreadAccessDeniedException;
import com.marketplace.hexagonal.Domain.Inquiry.Exceptions.InquiryThreadNotAvailableException;
import com.marketplace.hexagonal.Domain.Inquiry.Exceptions.InquiryThreadNotFoundException;

import com.marketplace.hexagonal.Domain.User.Exceptions.AddressNotFoundException;
import com.marketplace.hexagonal.Domain.User.Exceptions.PaymentMethodNotFoundException;
import com.marketplace.hexagonal.Domain.User.Exceptions.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ProblemDetail handleBadCredentials(Exception ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(DisabledException.class)
    public ProblemDetail handleDisabledAccount(DisabledException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED, "Account is disabled. Please contact support.");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(LockedException.class)
    public ProblemDetail handleLockedAccount(LockedException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED, "Account is locked. Please contact support.");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler({InvalidTokenException.class, ExpiredTokenException.class})
    public ProblemDetail handleInvalidToken(RuntimeException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(ChatRoomNotFoundException.class)
    public ProblemDetail handleChatRoomNotFound(ChatRoomNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(ChatRoomAccessDeniedException.class)
    public ProblemDetail handleChatRoomAccessDenied(ChatRoomAccessDeniedException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(ChatRoomNotAvailableException.class)
    public ProblemDetail handleChatRoomNotAvailable(ChatRoomNotAvailableException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(InquiryThreadNotFoundException.class)
    public ProblemDetail handleInquiryThreadNotFound(InquiryThreadNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(InquiryThreadAccessDeniedException.class)
    public ProblemDetail handleInquiryThreadAccessDenied(InquiryThreadAccessDeniedException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(InquiryThreadNotAvailableException.class)
    public ProblemDetail handleInquiryThreadNotAvailable(InquiryThreadNotAvailableException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ProblemDetail handleReviewNotFound(ReviewNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(ReviewAlreadyExistsException.class)
    public ProblemDetail handleReviewAlreadyExists(ReviewAlreadyExistsException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(ReviewAccessDeniedException.class)
    public ProblemDetail handleReviewAccessDenied(ReviewAccessDeniedException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(InvalidRatingException.class)
    public ProblemDetail handleInvalidRating(InvalidRatingException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(CannotReviewOwnContentException.class)
    public ProblemDetail handleCannotReviewOwn(CannotReviewOwnContentException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(FavoriteNotFoundException.class)
    public ProblemDetail handleFavoriteNotFound(FavoriteNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(FavoriteAlreadyExistsException.class)
    public ProblemDetail handleFavoriteAlreadyExists(FavoriteAlreadyExistsException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(SellerProfileNotFoundException.class)
    public ProblemDetail handleSellerNotFound(SellerProfileNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(SellerBannedException.class)
    public ProblemDetail handleSellerBanned(SellerBannedException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(SellerAlreadyAppliedException.class)
    public ProblemDetail handleSellerAlreadyApplied(SellerAlreadyAppliedException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(InvalidSellerStatusTransitionException.class)
    public ProblemDetail handleInvalidSellerTransition(InvalidSellerStatusTransitionException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public ProblemDetail handleAddressNotFound(AddressNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(PaymentMethodNotFoundException.class)
    public ProblemDetail handlePaymentMethodNotFound(PaymentMethodNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler({CategoryNotFoundException.class,
                       ProductNotFoundException.class,
                       ProductVariantNotFoundException.class,
                       ProductImageNotFoundException.class})
    public ProblemDetail handleProductResourceNotFound(RuntimeException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(SlugAlreadyExistsException.class)
    public ProblemDetail handleSlugConflict(SlugAlreadyExistsException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(DuplicateSkuException.class)
    public ProblemDetail handleDuplicateSku(DuplicateSkuException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(InvalidProductStatusTransitionException.class)
    public ProblemDetail handleInvalidProductTransition(InvalidProductStatusTransitionException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(ProductOwnershipException.class)
    public ProblemDetail handleProductOwnership(ProductOwnershipException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(VariantRequiredException.class)
    public ProblemDetail handleVariantRequired(VariantRequiredException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler({CartNotFoundException.class, CartItemNotFoundException.class})
    public ProblemDetail handleCartNotFound(RuntimeException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ProblemDetail handleInsufficientStock(InsufficientStockException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(CartAlreadyCheckedOutException.class)
    public ProblemDetail handleCartCheckedOut(CartAlreadyCheckedOutException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(CouponNotFoundException.class)
    public ProblemDetail handleCouponNotFound(CouponNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler({CouponInactiveException.class, CouponExpiredException.class,
                       CouponMaxUsesReachedException.class})
    public ProblemDetail handleCouponNotApplicable(RuntimeException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(CouponMinimumOrderNotMetException.class)
    public ProblemDetail handleCouponMinimumNotMet(CouponMinimumOrderNotMetException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ProblemDetail handleOrderNotFound(OrderNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(EmptyCartException.class)
    public ProblemDetail handleEmptyCart(EmptyCartException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(OrderAccessDeniedException.class)
    public ProblemDetail handleOrderAccessDenied(OrderAccessDeniedException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(SubOrderNotFoundException.class)
    public ProblemDetail handleSubOrderNotFound(SubOrderNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(SubOrderAccessDeniedException.class)
    public ProblemDetail handleSubOrderAccessDenied(SubOrderAccessDeniedException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(OrderCancellationNotAllowedException.class)
    public ProblemDetail handleOrderCancellationNotAllowed(OrderCancellationNotAllowedException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(ShippingOptionNotFoundException.class)
    public ProblemDetail handleShippingOptionNotFound(ShippingOptionNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(ShippingOptionOwnershipException.class)
    public ProblemDetail handleShippingOptionOwnership(ShippingOptionOwnershipException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ProblemDetail handleMissingHeader(MissingRequestHeaderException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Required header '" + ex.getHeaderName() + "' is missing.");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ProblemDetail handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Content-Type '" + ex.getContentType() + "' is not supported. Use application/json.");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value",
                        (a, b) -> a
                ));

        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Validation failed.");
        detail.setProperty("timestamp", Instant.now());
        detail.setProperty("errors", errors);
        return detail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception ex) {
        log.error("Unhandled exception", ex);
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        detail.setProperty("timestamp", Instant.now());
        return detail;
    }
}
