package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Order.Services.OrderService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Order.Models.SubOrder;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.Order.Exceptions.SubOrderNotFoundException;
import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerProfileNotFoundException;
import com.marketplace.hexagonal.Domain.User.Exceptions.UserNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.DTO.Order.OrderResponseDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Order.SubOrderResponseDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Order.UpdateSubOrderStatusRequestDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seller/orders")
@AllArgsConstructor
public class SellerOrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final SellerProfileRepositoryPort sellerRepo;

    @GetMapping
    public List<SubOrderResponseDTO> listSubOrders(Authentication auth) {
        SellerProfileId sellerId = resolveSellerProfileId(auth);
        return orderService.getSellerSubOrders(sellerId).stream()
                .map(SubOrderResponseDTO::from)
                .toList();
    }

    @GetMapping("/{subOrderId}")
    public SubOrderResponseDTO getSubOrder(@PathVariable String subOrderId,
                                           Authentication auth) {
        SellerProfileId sellerId = resolveSellerProfileId(auth);

        return orderService.getSellerSubOrders(sellerId).stream()
                .filter(s -> s.getId().getValue().equals(subOrderId))
                .map(SubOrderResponseDTO::from)
                .findFirst()
                .orElseThrow(() -> new SubOrderNotFoundException(subOrderId));
    }

    @PatchMapping("/{subOrderId}/status")
    public SubOrderResponseDTO updateStatus(@PathVariable String subOrderId,
                                            @Valid @RequestBody UpdateSubOrderStatusRequestDTO dto,
                                            Authentication auth) {
        SellerProfileId sellerId = resolveSellerProfileId(auth);
        SubOrder updated = orderService.updateSubOrderStatus(
                sellerId, subOrderId, dto.status());
        return SubOrderResponseDTO.from(updated);
    }

    @PatchMapping("/{orderId}/cancel")
    public OrderResponseDTO cancelOrder(@PathVariable String orderId, Authentication auth) {
        SellerProfileId sellerId = resolveSellerProfileId(auth);
        return OrderResponseDTO.from(orderService.sellerCancelOrder(sellerId, orderId));
    }

    private SellerProfileId resolveSellerProfileId(Authentication auth) {
        UserId userId = userService.findByEmail(auth.getName())
                .map(u -> u.getUserId())
                .orElseThrow(() -> new UserNotFoundException(auth.getName()));

        return sellerRepo.findByUserId(userId)
                .map(SellerProfile::getId)
                .orElseThrow(() -> new SellerProfileNotFoundException(userId.getValue()));
    }
}
