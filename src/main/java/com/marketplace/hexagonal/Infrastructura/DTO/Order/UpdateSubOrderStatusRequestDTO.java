package com.marketplace.hexagonal.Infrastructura.DTO.Order;

import com.marketplace.hexagonal.Domain.Order.Models.SubOrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateSubOrderStatusRequestDTO(
        @NotNull(message = "status must not be null")
        SubOrderStatus status
) {}
