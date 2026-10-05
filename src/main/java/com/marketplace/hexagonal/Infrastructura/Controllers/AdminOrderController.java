package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Order.Services.OrderService;
import com.marketplace.hexagonal.Domain.Order.Models.AdminOrderSearchCriteria;
import com.marketplace.hexagonal.Domain.Order.Models.OrderStatus;
import com.marketplace.hexagonal.Domain.Product.Models.PageResult;
import com.marketplace.hexagonal.Infrastructura.DTO.Order.AdminOrderSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import com.marketplace.hexagonal.Infrastructura.DTO.Order.OrderResponseDTO;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<PageResult<AdminOrderSummaryDTO>> listOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        AdminOrderSearchCriteria criteria =
                new AdminOrderSearchCriteria(status, userId, dateFrom, dateTo, page, size);

        PageResult<AdminOrderSummaryDTO> result =
                orderService.listAllOrders(criteria)
                        .map(AdminOrderSummaryDTO::from);

        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{orderId}/cancel")
    public OrderResponseDTO cancelOrder(@PathVariable String orderId) {
        return OrderResponseDTO.from(orderService.adminCancelOrder(orderId));
    }
}
