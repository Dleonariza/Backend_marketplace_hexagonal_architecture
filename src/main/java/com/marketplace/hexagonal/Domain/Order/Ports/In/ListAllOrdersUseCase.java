package com.marketplace.hexagonal.Domain.Order.Ports.In;

import com.marketplace.hexagonal.Domain.Order.Models.AdminOrderSearchCriteria;
import com.marketplace.hexagonal.Domain.Order.Models.Order;
import com.marketplace.hexagonal.Domain.Product.Models.PageResult;

public interface ListAllOrdersUseCase {
    PageResult<Order> listAll(AdminOrderSearchCriteria criteria);
}
