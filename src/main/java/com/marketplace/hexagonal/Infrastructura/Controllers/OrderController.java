package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Order.Services.OrderService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Order.Models.Order;
import com.marketplace.hexagonal.Domain.User.Exceptions.UserNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.DTO.Order.OrderResponseDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Order.PlaceOrderRequestDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDTO placeOrder(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody PlaceOrderRequestDTO dto,
            Authentication auth) {
        UserId userId = resolveUserId(auth);
        Order order = orderService.placeOrder(
                userId,
                idempotencyKey,
                dto.shippingAddressId(),
                dto.shippingSelections(),
                dto.paymentMethodId());
        return OrderResponseDTO.from(order);
    }

    @GetMapping
    public List<OrderResponseDTO> listOrders(Authentication auth) {
        UserId userId = resolveUserId(auth);
        return orderService.listOrders(userId).stream()
                .map(OrderResponseDTO::from)
                .toList();
    }

    @GetMapping("/{orderId}")
    public OrderResponseDTO getOrder(@PathVariable String orderId, Authentication auth) {
        UserId userId = resolveUserId(auth);
        return OrderResponseDTO.from(orderService.getOrder(userId, orderId));
    }

    @PatchMapping("/{orderId}/cancel")
    public OrderResponseDTO cancelOrder(@PathVariable String orderId, Authentication auth) {
        UserId userId = resolveUserId(auth);
        return OrderResponseDTO.from(orderService.cancelOrder(userId, orderId));
    }

    private UserId resolveUserId(Authentication auth) {
        return userService.findByEmail(auth.getName())
                .map(u -> u.getUserId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Authenticated user not found: " + auth.getName()));
    }
}
