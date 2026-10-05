package com.marketplace.hexagonal.Domain.Inquiry.Models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class InquiryMessageId {

    private final String value;

    private InquiryMessageId(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("InquiryMessageId cannot be null or blank");
        this.value = value;
    }

    public static InquiryMessageId generate() { return new InquiryMessageId(UUID.randomUUID().toString()); }
    public static InquiryMessageId of(String value) { return new InquiryMessageId(value); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InquiryMessageId c)) return false;
        return value.equals(c.value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
