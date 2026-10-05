package com.marketplace.hexagonal.Domain.Review.Exceptions;

import com.marketplace.hexagonal.Domain.Review.Models.ReviewTargetType;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public class ReviewAlreadyExistsException extends RuntimeException {
    public ReviewAlreadyExistsException(UserId authorId, ReviewTargetType targetType, String targetId) {
        super("User " + authorId.getValue() + " has already reviewed "
                + targetType.name().toLowerCase() + " " + targetId);
    }
}
