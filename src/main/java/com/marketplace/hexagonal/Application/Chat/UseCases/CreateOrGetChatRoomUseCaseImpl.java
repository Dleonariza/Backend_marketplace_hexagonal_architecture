package com.marketplace.hexagonal.Application.Chat.UseCases;

import com.marketplace.hexagonal.Domain.Chat.Exceptions.ChatRoomAccessDeniedException;
import com.marketplace.hexagonal.Domain.Chat.Exceptions.ChatRoomNotAvailableException;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoom;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomType;
import com.marketplace.hexagonal.Domain.Chat.Ports.In.CreateOrGetChatRoomUseCase;
import com.marketplace.hexagonal.Domain.Chat.Ports.Out.ChatRoomRepositoryPort;
import com.marketplace.hexagonal.Domain.Order.Models.Order;
import com.marketplace.hexagonal.Domain.Order.Models.OrderId;
import com.marketplace.hexagonal.Domain.Order.Models.SubOrder;
import com.marketplace.hexagonal.Domain.Order.Ports.Out.OrderRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class CreateOrGetChatRoomUseCaseImpl implements CreateOrGetChatRoomUseCase {

    private final ChatRoomRepositoryPort chatRoomRepo;
    private final OrderRepositoryPort orderRepo;
    private final SellerProfileRepositoryPort sellerProfileRepo;

    @Override
    public ChatRoom execute(UserId requesterId,
                            boolean isAdmin,
                            String orderId,
                            ChatRoomType type,
                            String sellerProfileId) {
        Order order = orderRepo.findById(OrderId.of(orderId))
                .orElseThrow(() -> new ChatRoomNotAvailableException(
                        "Order not found: " + orderId));

        return switch (type) {
            case BUYER_SELLER -> handleBuyerSeller(requesterId, order, sellerProfileId);
            case BUYER_SUPPORT -> handleBuyerSupport(requesterId, isAdmin, order);
        };
    }

    private ChatRoom handleBuyerSeller(UserId requesterId, Order order, String sellerProfileId) {
        if (sellerProfileId == null || sellerProfileId.isBlank())
            throw new ChatRoomNotAvailableException(
                    "sellerProfileId is required for BUYER_SELLER rooms.");

        SellerProfileId spId = SellerProfileId.of(sellerProfileId);

        boolean orderHasThisSeller = order.getSubOrders().stream()
                .map(SubOrder::getSellerId)
                .anyMatch(spId::equals);

        if (!orderHasThisSeller)
            throw new ChatRoomNotAvailableException(
                    "No sub-order for seller " + sellerProfileId + " in order " + order.getId().getValue());

        SellerProfile sellerProfile = sellerProfileRepo.findById(spId)
                .orElseThrow(() -> new ChatRoomNotAvailableException(
                        "Seller profile not found: " + sellerProfileId));

        UserId buyerId = order.getUserId();
        UserId sellerUserId = sellerProfile.getUserId();

        if (!requesterId.equals(buyerId) && !requesterId.equals(sellerUserId))
            throw new ChatRoomAccessDeniedException();

        Optional<ChatRoom> existing = chatRoomRepo.findByOrderIdAndTypeAndSellerUserId(
                order.getId().getValue(), ChatRoomType.BUYER_SELLER, sellerUserId);

        return existing.orElseGet(() ->
                chatRoomRepo.save(ChatRoom.create(
                        order.getId().getValue(),
                        ChatRoomType.BUYER_SELLER,
                        buyerId,
                        sellerUserId)));
    }

    private ChatRoom handleBuyerSupport(UserId requesterId, boolean isAdmin, Order order) {
        UserId buyerId = order.getUserId();

        if (!requesterId.equals(buyerId) && !isAdmin)
            throw new ChatRoomAccessDeniedException();

        Optional<ChatRoom> existing = chatRoomRepo.findByOrderIdAndTypeAndSellerUserId(
                order.getId().getValue(), ChatRoomType.BUYER_SUPPORT, null);

        return existing.orElseGet(() ->
                chatRoomRepo.save(ChatRoom.create(
                        order.getId().getValue(),
                        ChatRoomType.BUYER_SUPPORT,
                        buyerId,
                        null)));
    }
}
