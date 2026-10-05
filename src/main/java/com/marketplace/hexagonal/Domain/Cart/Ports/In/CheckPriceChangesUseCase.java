package com.marketplace.hexagonal.Domain.Cart.Ports.In;

import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Cart.Models.PriceChangeInfo;

import java.util.List;

public interface CheckPriceChangesUseCase {
    List<PriceChangeInfo> checkPriceChanges(Cart cart);
}
