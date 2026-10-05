package com.marketplace.hexagonal.Infrastructura.DTO.Seller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSellerProfileRequestDTO(

        @NotBlank(message = "Store name is required.")
        @Size(min = 2, max = 100, message = "Store name must be between 2 and 100 characters.")
        String storeName,

        @NotBlank(message = "Store description is required.")
        @Size(max = 1000, message = "Store description must not exceed 1000 characters.")
        String storeDescription,

        @NotBlank(message = "Main category is required.")
        String mainCategory,

        @NotBlank(message = "Contact email is required.")
        @Email(message = "Contact email must be a valid email address.")
        String contactEmail,

        @NotBlank(message = "Contact phone is required.")
        String contactPhone,

        String logoUrl
) {}
