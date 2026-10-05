package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Inquiry.Services.InquiryService;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThreadId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.DTO.Inquiry.SendInquiryMessageRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class InquiryWebSocketController {

    private final InquiryService inquiryService;
    private final UserRepositoryPort userRepo;

    @MessageMapping("/inquiry.send")
    public void handleMessage(@Payload SendInquiryMessageRequestDTO request, Principal principal) {
        String email = principal.getName();

        UserModel user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user not found: " + email));

        UserId senderId = user.getUserId();
        String senderName = user.getName() != null ? user.getName() : user.getUserName();

        inquiryService.sendMessage(
                InquiryThreadId.of(request.threadId()), senderId, senderName, request.content());

        log.debug("Inquiry message persisted and event published for thread {}", request.threadId());
    }
}
