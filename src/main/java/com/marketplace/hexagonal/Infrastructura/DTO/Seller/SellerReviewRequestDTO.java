package com.marketplace.hexagonal.Infrastructura.DTO.Seller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SellerReviewRequestDTO(
        @NotBlank(message = "Reason is required")
        @Size(max = 500, message = "Reason cannot exceed 500 characters")
        String reason
) {}
