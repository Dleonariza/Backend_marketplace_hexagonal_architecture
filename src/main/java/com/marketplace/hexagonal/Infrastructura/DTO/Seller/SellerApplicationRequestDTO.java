package com.marketplace.hexagonal.Infrastructura.DTO.Seller;

import com.marketplace.hexagonal.Domain.Seller.Models.AccountType;
import com.marketplace.hexagonal.Domain.Seller.Models.DocumentType;
import jakarta.validation.constraints.*;

public record SellerApplicationRequestDTO(

        @NotBlank(message = "Store name is required")
        @Size(min = 2, max = 100, message = "Store name must be between 2 and 100 characters")
        String storeName,

        @NotBlank(message = "Store description is required")
        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String storeDescription,

        @NotBlank(message = "Main category is required")
        String mainCategory,

        @NotBlank(message = "Contact email is required")
        @Email(message = "Contact email must be valid")
        String contactEmail,

        @NotBlank(message = "Contact phone is required")
        String contactPhone,

        String logoUrl,

        @NotNull(message = "Document type is required")
        DocumentType documentType,

        @NotBlank(message = "Document number is required")
        String documentNumber,

        @NotBlank(message = "Bank name is required")
        String bankName,

        @NotBlank(message = "Account number is required")
        String accountNumber,

        @NotNull(message = "Account type is required")
        AccountType accountType,

        @NotBlank(message = "Account holder name is required")
        String accountHolderName,

        @NotBlank(message = "Account holder document is required")
        String accountHolderDocument
) {}
