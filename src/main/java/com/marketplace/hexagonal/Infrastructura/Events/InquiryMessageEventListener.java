package com.marketplace.hexagonal.Infrastructura.Events;

import com.marketplace.hexagonal.Domain.Inquiry.Events.InquiryMessageSentEvent;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Adapters.Websocket.InquiryBroadcastService;
import com.marketplace.hexagonal.Infrastructura.DTO.Inquiry.InquiryMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InquiryMessageEventListener {

    private final InquiryBroadcastService broadcastService;
    private final UserRepositoryPort userRepo;

    @EventListener
    public void onInquiryMessageSent(InquiryMessageSentEvent event) {
        InquiryMessageDTO dto = InquiryMessageDTO.from(event.message());
        String threadId = event.threadId().getValue();

        try {
            broadcastService.broadcastToThread(threadId, dto);
            notifyOtherParticipant(event, dto);
        } catch (Exception ex) {
            log.error("Failed to broadcast inquiry message (threadId={}, messageId={}): {}",
                    threadId, dto.messageId(), ex.getMessage(), ex);
        }
    }

    private void notifyOtherParticipant(InquiryMessageSentEvent event, InquiryMessageDTO dto) {
        String senderId = event.message().getSenderId().getValue();
        boolean senderIsBuyer = event.buyerId().getValue().equals(senderId);
        UserId recipientId = senderIsBuyer ? event.sellerUserId() : event.buyerId();

        try {
            UserModel user = userRepo.findById(recipientId);
            broadcastService.notifyUser(user.getEmail(), dto);
        } catch (Exception ex) {
            log.warn("Cannot notify user '{}': {}", recipientId.getValue(), ex.getMessage());
        }
    }
}
