package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Inquiry.Services.InquiryService;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryMessage;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThread;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThreadId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.DTO.Inquiry.InquiryMessageDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Inquiry.InquiryPageResponseDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Inquiry.InquiryThreadResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;
    private final UserRepositoryPort userRepo;

    @PostMapping("/products/{productId}/inquiries")
    public ResponseEntity<InquiryThreadResponseDTO> getOrCreateThread(
            @PathVariable String productId,
            Authentication authentication) {
        UserId buyerId = resolveUserId(authentication);
        InquiryThread thread = inquiryService.getOrCreateThread(buyerId, productId);
        return ResponseEntity.ok(InquiryThreadResponseDTO.from(thread));
    }

    @GetMapping("/inquiries")
    public ResponseEntity<List<InquiryThreadResponseDTO>> listThreads(Authentication authentication) {
        UserId userId = resolveUserId(authentication);
        List<InquiryThreadResponseDTO> threads = inquiryService.getUserThreads(userId)
                .stream()
                .map(InquiryThreadResponseDTO::from)
                .toList();
        return ResponseEntity.ok(threads);
    }

    @GetMapping("/inquiries/{threadId}/messages")
    public ResponseEntity<InquiryPageResponseDTO> getMessages(
            @PathVariable String threadId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        UserId requesterId = resolveUserId(authentication);
        InquiryThreadId tid = InquiryThreadId.of(threadId);

        List<InquiryMessage> messages = inquiryService.getMessages(tid, requesterId, page, size);
        long total = inquiryService.countMessages(tid, requesterId);

        List<InquiryMessageDTO> dtos = messages.stream().map(InquiryMessageDTO::from).toList();
        return ResponseEntity.ok(new InquiryPageResponseDTO(dtos, page, size, total));
    }

    @PostMapping("/inquiries/{threadId}/close")
    public ResponseEntity<InquiryThreadResponseDTO> closeThread(
            @PathVariable String threadId,
            Authentication authentication) {
        UserId requesterId = resolveUserId(authentication);
        InquiryThread closed = inquiryService.closeThread(InquiryThreadId.of(threadId), requesterId);
        return ResponseEntity.ok(InquiryThreadResponseDTO.from(closed));
    }

    private UserId resolveUserId(Authentication authentication) {
        String email = authentication.getName();
        return userRepo.findByEmail(email)
                .map(u -> u.getUserId())
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user not found: " + email));
    }
}
