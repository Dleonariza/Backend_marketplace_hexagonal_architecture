package com.marketplace.hexagonal.Domain.Inquiry.Exceptions;

public class InquiryThreadNotAvailableException extends RuntimeException {
    public InquiryThreadNotAvailableException(String message) {
        super(message);
    }
}
