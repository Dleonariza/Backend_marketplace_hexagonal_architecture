package com.marketplace.hexagonal.Domain.Admin.Ports.In;

import com.marketplace.hexagonal.Domain.Admin.Models.CommissionConfig;

public interface GetCommissionConfigUseCase {
    CommissionConfig getConfig();
}
