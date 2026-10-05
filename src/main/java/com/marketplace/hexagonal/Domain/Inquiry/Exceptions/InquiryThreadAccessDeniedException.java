package com.marketplace.hexagonal.Domain.Inquiry.Exceptions;

public class InquiryThreadAccessDeniedException extends RuntimeException {
    public InquiryThreadAccessDeniedException() {
        super("You are not a participant of this inquiry thread.");
    }
}
