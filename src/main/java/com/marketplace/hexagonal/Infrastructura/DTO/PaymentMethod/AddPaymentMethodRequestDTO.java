package com.marketplace.hexagonal.Infrastructura.DTO.PaymentMethod;

import jakarta.validation.constraints.*;

public record AddPaymentMethodRequestDTO(

        String stripePaymentMethodId,

        @NotBlank(message = "last4 is required")
        @Size(min = 4, max = 4, message = "last4 must be exactly 4 digits")
        @Pattern(regexp = "\\d{4}", message = "last4 must contain only digits")
        String last4,

        @NotBlank(message = "brand is required")
        String brand,

        @Min(value = 1, message = "expMonth must be between 1 and 12")
        @Max(value = 12, message = "expMonth must be between 1 and 12")
        int expMonth,

        @Min(value = 2000, message = "expYear is invalid")
        int expYear,

        @NotBlank(message = "cardholderName is required")
        String cardholderName,

        Boolean isDefault
) {}
