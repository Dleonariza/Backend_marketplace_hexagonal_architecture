package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Chat.Services.ChatService;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatMessage;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.DTO.Chat.ChatMessageDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Chat.SendMessageRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final UserRepositoryPort userRepo;

    @MessageMapping("/chat.send")
    public void handleMessage(@Payload SendMessageRequestDTO request,
                              Principal principal) {
        String email = principal.getName();
        boolean isAdmin = hasAdminRole(principal);

        var user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user not found: " + email));

        UserId senderId = user.getUserId();
        String senderName = user.getName() != null ? user.getName() : user.getUserName();

        ChatMessage message = chatService.sendMessage(
                ChatRoomId.of(request.roomId()),
                senderId,
                senderName,
                isAdmin,
                request.content());

        log.debug("Message {} persisted and event published for room {}",
                message.getId().getValue(), request.roomId());
    }

    private boolean hasAdminRole(Principal principal) {
        if (principal instanceof Authentication auth) {
            return auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(a -> a.equals("ROLE_ADMIN"));
        }
        return false;
    }
}
