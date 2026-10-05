package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Admin.Services.AdminService;
import com.marketplace.hexagonal.Infrastructura.DTO.Admin.CommissionConfigResponseDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Admin.UpdateCommissionRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/commission")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCommissionController {

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<CommissionConfigResponseDTO> getCommission() {
        return ResponseEntity.ok(
                CommissionConfigResponseDTO.from(adminService.getConfig()));
    }

    @PutMapping
    public ResponseEntity<CommissionConfigResponseDTO> updateCommission(
            @Valid @RequestBody UpdateCommissionRequestDTO request) {
        return ResponseEntity.ok(
                CommissionConfigResponseDTO.from(
                        adminService.update(request.rate(), request.description())));
    }
}
