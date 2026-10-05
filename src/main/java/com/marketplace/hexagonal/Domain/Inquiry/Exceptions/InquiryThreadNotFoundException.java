package com.marketplace.hexagonal.Domain.Inquiry.Exceptions;

public class InquiryThreadNotFoundException extends RuntimeException {
    public InquiryThreadNotFoundException(String threadId) {
        super("Inquiry thread not found: " + threadId);
    }
}
