package com.marketplace.hexagonal.Infrastructura.Events;

import com.marketplace.hexagonal.Application.Chat.Services.ChatService;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatMessage;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoom;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomId;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomType;
import com.marketplace.hexagonal.Domain.Chat.Ports.Out.ChatMessageRepositoryPort;
import com.marketplace.hexagonal.Domain.Chat.Ports.Out.ChatRoomRepositoryPort;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryMessage;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryStatus;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThread;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryMessageRepositoryPort;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryThreadRepositoryPort;
import com.marketplace.hexagonal.Domain.Order.Events.OrderPlacedEvent;
import com.marketplace.hexagonal.Domain.Order.Models.*;
import com.marketplace.hexagonal.Domain.Order.Ports.Out.OrderRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InquiryToChatMigrationListenerTest {

    @Mock OrderRepositoryPort orderRepo;
    @Mock InquiryThreadRepositoryPort inquiryThreadRepo;
    @Mock InquiryMessageRepositoryPort inquiryMessageRepo;
    @Mock ChatService chatService;
    @Mock ChatMessageRepositoryPort chatMessageRepo;
    @Mock ChatRoomRepositoryPort chatRoomRepo;
    InquiryToChatMigrationListener listener;

    private static final UserId BUYER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final UserId SELLER_UID = UserId.of("bc66d237-f54d-4bad-b521-0eba3b06304c");
    private static final SellerProfileId SELLER_ID = SellerProfileId.of("cc000000-0000-0000-0000-000000000001");
    private static final ProductId PRODUCT_ID = ProductId.of("aa000000-0000-0000-0000-000000000001");
    private static final OrderId ORDER_ID = OrderId.generate();

    @BeforeEach
    void setUp() {
        listener = new InquiryToChatMigrationListener(
                orderRepo, inquiryThreadRepo, inquiryMessageRepo, chatService, chatMessageRepo, chatRoomRepo);
    }

    @Test
    void onOrderPlaced_matchingOpenThread_migratesMessagesAndDeletesThread() {
        Order order = buildOrder();
        InquiryThread thread = InquiryThread.create(PRODUCT_ID, BUYER_ID, SELLER_UID);
        ChatRoom room = ChatRoom.create(ORDER_ID.getValue(), ChatRoomType.BUYER_SELLER, BUYER_ID, SELLER_UID);
        InquiryMessage msg1 = InquiryMessage.create(thread.getId(), BUYER_ID, "Juan", "Is this in stock?");
        InquiryMessage msg2 = InquiryMessage.create(thread.getId(), SELLER_UID, "Store", "Yes!");

        when(orderRepo.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(inquiryThreadRepo.findByBuyerIdAndProductId(BUYER_ID, PRODUCT_ID)).thenReturn(Optional.of(thread));
        when(chatService.getOrCreateBuyerSellerRoom(BUYER_ID, false, ORDER_ID.getValue(), SELLER_ID.getValue()))
                .thenReturn(room);
        when(inquiryMessageRepo.findAllByThreadIdOrderBySentAtAsc(thread.getId()))
                .thenReturn(List.of(msg1, msg2));

        listener.onOrderPlaced(new OrderPlacedEvent(ORDER_ID, BUYER_ID));

        ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatMessageRepo, times(2)).save(captor.capture());
        List<ChatMessage> saved = captor.getAllValues();

        assertThat(saved.get(0).getSenderId()).isEqualTo(msg1.getSenderId());
        assertThat(saved.get(0).getSenderName()).isEqualTo(msg1.getSenderName());
        assertThat(saved.get(0).getContent()).isEqualTo(msg1.getContent());
        assertThat(saved.get(0).getSentAt()).isEqualTo(msg1.getSentAt());

        assertThat(saved.get(1).getSenderId()).isEqualTo(msg2.getSenderId());
        assertThat(saved.get(1).getSenderName()).isEqualTo(msg2.getSenderName());
        assertThat(saved.get(1).getContent()).isEqualTo(msg2.getContent());
        assertThat(saved.get(1).getSentAt()).isEqualTo(msg2.getSentAt());

        verify(chatRoomRepo).updateLastActivity(eq(room.getId()), any());
        verify(inquiryThreadRepo).deleteById(thread.getId());
    }

    @Test
    void onOrderPlaced_noMatchingThread_doesNothing() {
        Order order = buildOrder();
        when(orderRepo.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(inquiryThreadRepo.findByBuyerIdAndProductId(BUYER_ID, PRODUCT_ID)).thenReturn(Optional.empty());

        listener.onOrderPlaced(new OrderPlacedEvent(ORDER_ID, BUYER_ID));

        verifyNoInteractions(chatService, chatMessageRepo, chatRoomRepo);
        verify(inquiryThreadRepo, never()).deleteById(any());
    }

    @Test
    void onOrderPlaced_closedThread_isNotMigrated() {
        Order order = buildOrder();
        InquiryThread closed = InquiryThread.create(PRODUCT_ID, BUYER_ID, SELLER_UID);
        closed.close();
        when(orderRepo.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(inquiryThreadRepo.findByBuyerIdAndProductId(BUYER_ID, PRODUCT_ID)).thenReturn(Optional.of(closed));

        listener.onOrderPlaced(new OrderPlacedEvent(ORDER_ID, BUYER_ID));

        verifyNoInteractions(chatService);
        verify(inquiryThreadRepo, never()).deleteById(any());
    }

    @Test
    void onOrderPlaced_chatServiceThrows_exceptionIsSwallowedAndThreadIsNotDeleted() {
        Order order = buildOrder();
        InquiryThread thread = InquiryThread.create(PRODUCT_ID, BUYER_ID, SELLER_UID);
        when(orderRepo.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(inquiryThreadRepo.findByBuyerIdAndProductId(BUYER_ID, PRODUCT_ID)).thenReturn(Optional.of(thread));
        when(chatService.getOrCreateBuyerSellerRoom(any(), anyBoolean(), any(), any()))
                .thenThrow(new RuntimeException("db down"));

        listener.onOrderPlaced(new OrderPlacedEvent(ORDER_ID, BUYER_ID));

        verify(inquiryThreadRepo, never()).deleteById(any());
    }

    private Order buildOrder() {
        OrderItem item = OrderItem.builder()
                .id(OrderItemId.generate())
                .productId(PRODUCT_ID)
                .sellerId(SELLER_ID)
                .productName("Test Product")
                .quantity(1)
                .unitPrice(Money.of(new BigDecimal("10.00"), "USD"))
                .itemSubtotal(Money.of(new BigDecimal("10.00"), "USD"))
                .variantAttributes(Map.of())
                .build();

        SubOrder subOrder = SubOrder.reconstitute(
                SubOrderId.generate(), ORDER_ID, SELLER_ID, "Test Store",
                List.of(item),
                Money.of(new BigDecimal("10.00"), "USD"),
                Money.of(BigDecimal.ZERO, "USD"),
                Money.of(new BigDecimal("10.00"), "USD"),
                null, SubOrderStatus.PENDING_PAYMENT, Instant.now(), Instant.now());

        return Order.reconstitute(
                ORDER_ID, BUYER_ID, "O0000000001", "idem-key-1",
                List.of(subOrder),
                Money.of(new BigDecimal("10.00"), "USD"),
                Money.of(BigDecimal.ZERO, "USD"),
                Money.of(BigDecimal.ZERO, "USD"),
                Money.of(new BigDecimal("10.00"), "USD"),
                OrderStatus.PENDING_PAYMENT, "addr-1", null, null,
                Instant.now(), Instant.now());
    }
}
