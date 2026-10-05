package com.marketplace.hexagonal.Domain.Review.Models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ReviewId {

    private final String value;

    public ReviewId(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("ReviewId cannot be null or blank");
        this.value = value;
    }

    public static ReviewId generate() { return new ReviewId(UUID.randomUUID().toString()); }
    public static ReviewId of(String value) { return new ReviewId(value); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReviewId)) return false;
        return value.equals(((ReviewId) o).value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
