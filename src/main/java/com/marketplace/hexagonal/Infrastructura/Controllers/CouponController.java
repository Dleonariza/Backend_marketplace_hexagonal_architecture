package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Coupon.Services.CouponService;
import com.marketplace.hexagonal.Domain.Coupon.Models.Coupon;
import com.marketplace.hexagonal.Infrastructura.DTO.Coupon.CouponResponseDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Coupon.CreateCouponRequestDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/coupons")
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<CouponResponseDTO> create(@Valid @RequestBody CreateCouponRequestDTO body) {
        Coupon coupon = couponService.createCoupon(
                body.code(),
                body.description(),
                body.discountType(),
                body.discountValue(),
                body.minimumOrderAmount(),
                body.maxUses(),
                body.validFrom(),
                body.validUntil());
        return ResponseEntity.status(HttpStatus.CREATED).body(CouponResponseDTO.from(coupon));
    }

    @GetMapping
    public ResponseEntity<List<CouponResponseDTO>> listAll() {
        List<CouponResponseDTO> list = couponService.listAllCoupons()
                .stream()
                .map(CouponResponseDTO::from)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{code}")
    public ResponseEntity<CouponResponseDTO> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(CouponResponseDTO.from(couponService.getCouponByCode(code)));
    }

    @PatchMapping("/{code}/activate")
    public ResponseEntity<Void> activate(@PathVariable String code) {
        couponService.activateCoupon(code);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{code}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable String code) {
        couponService.deactivateCoupon(code);
        return ResponseEntity.noContent().build();
    }
}
