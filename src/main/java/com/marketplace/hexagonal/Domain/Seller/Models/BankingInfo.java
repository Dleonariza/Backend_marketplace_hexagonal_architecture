package com.marketplace.hexagonal.Domain.Seller.Models;

public record BankingInfo(
        String bankName,
        String accountNumber,
        AccountType accountType,
        String accountHolderName,
        String accountHolderDocument
) {
    public BankingInfo {
        if (bankName == null || bankName.isBlank())
            throw new IllegalArgumentException("Bank name cannot be blank");
        if (accountNumber == null || accountNumber.isBlank())
            throw new IllegalArgumentException("Account number cannot be blank");
        if (accountType == null)
            throw new IllegalArgumentException("Account type cannot be null");
        if (accountHolderName == null || accountHolderName.isBlank())
            throw new IllegalArgumentException("Account holder name cannot be blank");
        if (accountHolderDocument == null || accountHolderDocument.isBlank())
            throw new IllegalArgumentException("Account holder document cannot be blank");
    }
}
