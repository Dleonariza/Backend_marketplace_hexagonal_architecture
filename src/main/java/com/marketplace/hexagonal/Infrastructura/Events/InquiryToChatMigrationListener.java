package com.marketplace.hexagonal.Infrastructura.Events;

import com.marketplace.hexagonal.Application.Chat.Services.ChatService;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatMessage;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatMessageId;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoom;
import com.marketplace.hexagonal.Domain.Chat.Ports.Out.ChatMessageRepositoryPort;
import com.marketplace.hexagonal.Domain.Chat.Ports.Out.ChatRoomRepositoryPort;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryMessage;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryStatus;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThread;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryMessageRepositoryPort;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryThreadRepositoryPort;
import com.marketplace.hexagonal.Domain.Order.Events.OrderPlacedEvent;
import com.marketplace.hexagonal.Domain.Order.Models.Order;
import com.marketplace.hexagonal.Domain.Order.Models.OrderItem;
import com.marketplace.hexagonal.Domain.Order.Models.SubOrder;
import com.marketplace.hexagonal.Domain.Order.Ports.Out.OrderRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InquiryToChatMigrationListener {

    private final OrderRepositoryPort orderRepo;
    private final InquiryThreadRepositoryPort inquiryThreadRepo;
    private final InquiryMessageRepositoryPort inquiryMessageRepo;
    private final ChatService chatService;
    private final ChatMessageRepositoryPort chatMessageRepo;
    private final ChatRoomRepositoryPort chatRoomRepo;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderPlaced(OrderPlacedEvent event) {
        try {
            orderRepo.findById(event.orderId()).ifPresent(this::migrateOrderInquiries);
        } catch (Exception ex) {
            log.error("Failed to migrate inquiries for order {}: {}",
                    event.orderId().getValue(), ex.getMessage(), ex);
        }
    }

    private void migrateOrderInquiries(Order order) {
        for (SubOrder subOrder : order.getSubOrders()) {
            for (OrderItem item : subOrder.getItems()) {
                migrateItemIfNeeded(order, subOrder.getSellerId(), item.getProductId());
            }
        }
    }

    private void migrateItemIfNeeded(Order order, SellerProfileId sellerId, ProductId productId) {
        try {
            inquiryThreadRepo.findByBuyerIdAndProductId(order.getUserId(), productId)
                    .filter(thread -> thread.getStatus() == InquiryStatus.OPEN)
                    .ifPresent(thread -> migrateThread(thread, order, sellerId));
        } catch (Exception ex) {
            log.error("Failed to migrate inquiry for product {} in order {}: {}",
                    productId.getValue(), order.getId().getValue(), ex.getMessage(), ex);
        }
    }

    private void migrateThread(InquiryThread thread, Order order, SellerProfileId sellerId) {
        try {
            ChatRoom room = chatService.getOrCreateBuyerSellerRoom(
                    order.getUserId(), false, order.getId().getValue(), sellerId.getValue());

            List<InquiryMessage> messages =
                    inquiryMessageRepo.findAllByThreadIdOrderBySentAtAsc(thread.getId());

            for (InquiryMessage message : messages) {
                ChatMessage migrated = ChatMessage.reconstitute(
                        ChatMessageId.generate(),
                        room.getId(),
                        message.getSenderId(),
                        message.getSenderName(),
                        message.getContent(),
                        message.getSentAt(),
                        true);
                chatMessageRepo.save(migrated);
            }

            if (!messages.isEmpty())
                chatRoomRepo.updateLastActivity(room.getId(), Instant.now());

            inquiryThreadRepo.deleteById(thread.getId());

            log.info("Migrated inquiry thread {} ({} messages) into chat room {}",
                    thread.getId().getValue(), messages.size(), room.getId().getValue());
        } catch (Exception ex) {
            log.error("Failed to migrate inquiry thread {}: {}",
                    thread.getId().getValue(), ex.getMessage(), ex);
        }
    }
}
