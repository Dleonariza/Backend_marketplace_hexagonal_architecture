package com.marketplace.hexagonal.Domain.Admin.Ports.In;

import com.marketplace.hexagonal.Domain.Admin.Models.CommissionConfig;

import java.math.BigDecimal;

public interface UpdateCommissionConfigUseCase {
    CommissionConfig update(BigDecimal rate, String description);
}
