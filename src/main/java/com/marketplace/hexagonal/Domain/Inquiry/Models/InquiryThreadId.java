package com.marketplace.hexagonal.Domain.Inquiry.Models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class InquiryThreadId {

    private final String value;

    private InquiryThreadId(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("InquiryThreadId cannot be null or blank");
        this.value = value;
    }

    public static InquiryThreadId generate() { return new InquiryThreadId(UUID.randomUUID().toString()); }
    public static InquiryThreadId of(String value) { return new InquiryThreadId(value); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InquiryThreadId c)) return false;
        return value.equals(c.value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
