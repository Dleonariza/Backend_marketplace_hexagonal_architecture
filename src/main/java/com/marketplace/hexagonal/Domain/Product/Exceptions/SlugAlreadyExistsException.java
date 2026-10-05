package com.marketplace.hexagonal.Domain.Product.Exceptions;

public class SlugAlreadyExistsException extends RuntimeException {
    public SlugAlreadyExistsException(String slug) {
        super("The slug '" + slug + "' is already in use.");
    }
}
