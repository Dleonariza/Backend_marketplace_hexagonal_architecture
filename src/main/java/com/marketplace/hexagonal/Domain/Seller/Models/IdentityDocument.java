package com.marketplace.hexagonal.Domain.Seller.Models;

public record IdentityDocument(DocumentType documentType, String documentNumber) {

    public IdentityDocument {
        if (documentType == null)
            throw new IllegalArgumentException("Document type cannot be null");
        if (documentNumber == null || documentNumber.isBlank())
            throw new IllegalArgumentException("Document number cannot be blank");
    }
}
