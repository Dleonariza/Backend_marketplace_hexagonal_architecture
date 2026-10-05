package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Chat.Services.ChatService;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoom;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomId;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatMessage;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.DTO.Chat.ChatMessageDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Chat.ChatPageResponseDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Chat.ChatRoomResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserRepositoryPort userRepo;

    @PostMapping("/orders/{orderId}/chat/seller/{sellerProfileId}")
    public ResponseEntity<ChatRoomResponseDTO> getOrCreateBuyerSellerRoom(
            @PathVariable String orderId,
            @PathVariable String sellerProfileId,
            Authentication authentication) {
        UserId requesterId = resolveUserId(authentication);
        boolean isAdmin = hasAdminRole(authentication);

        ChatRoom room = chatService.getOrCreateBuyerSellerRoom(
                requesterId, isAdmin, orderId, sellerProfileId);

        return ResponseEntity.ok(ChatRoomResponseDTO.from(room));
    }

    @PostMapping("/orders/{orderId}/chat/support")
    public ResponseEntity<ChatRoomResponseDTO> getOrCreateSupportRoom(
            @PathVariable String orderId,
            Authentication authentication) {
        UserId requesterId = resolveUserId(authentication);
        boolean isAdmin = hasAdminRole(authentication);

        ChatRoom room = chatService.getOrCreateSupportRoom(requesterId, isAdmin, orderId);
        return ResponseEntity.ok(ChatRoomResponseDTO.from(room));
    }

    @GetMapping("/chat/rooms")
    public ResponseEntity<List<ChatRoomResponseDTO>> listRooms(Authentication authentication) {
        UserId userId = resolveUserId(authentication);
        boolean isAdmin = hasAdminRole(authentication);

        List<ChatRoomResponseDTO> rooms = chatService.getUserRooms(userId, isAdmin)
                .stream()
                .map(ChatRoomResponseDTO::from)
                .toList();

        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/chat/rooms/{roomId}/messages")
    public ResponseEntity<ChatPageResponseDTO> getMessages(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        UserId requesterId = resolveUserId(authentication);
        boolean isAdmin = hasAdminRole(authentication);
        ChatRoomId crid = ChatRoomId.of(roomId);

        List<ChatMessage> messages = chatService.getMessages(crid, requesterId, isAdmin, page, size);
        long total = chatService.countMessages(crid, requesterId, isAdmin);

        List<ChatMessageDTO> dtos = messages.stream().map(ChatMessageDTO::from).toList();

        return ResponseEntity.ok(new ChatPageResponseDTO(dtos, page, size, total));
    }

    private UserId resolveUserId(Authentication authentication) {
        String email = authentication.getName();
        return userRepo.findByEmail(email)
                .map(u -> u.getUserId())
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user not found: " + email));
    }

    private boolean hasAdminRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));
    }
}
