package com.marketplace.hexagonal.Domain.Inquiry.Models;

import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class InquiryMessage {

    private InquiryMessageId id;
    private InquiryThreadId threadId;
    private UserId senderId;

    private String senderName;
    private String content;
    private Instant sentAt;

    public static InquiryMessage create(InquiryThreadId threadId,
                                        UserId senderId,
                                        String senderName,
                                        String content) {
        if (content == null || content.isBlank())
            throw new IllegalArgumentException("Inquiry message content must not be blank.");
        return InquiryMessage.builder()
                .id(InquiryMessageId.generate())
                .threadId(threadId)
                .senderId(senderId)
                .senderName(senderName)
                .content(content.strip())
                .sentAt(Instant.now())
                .build();
    }

    public static InquiryMessage reconstitute(InquiryMessageId id,
                                              InquiryThreadId threadId,
                                              UserId senderId,
                                              String senderName,
                                              String content,
                                              Instant sentAt) {
        InquiryMessage m = new InquiryMessage();
        m.id = id;
        m.threadId = threadId;
        m.senderId = senderId;
        m.senderName = senderName;
        m.content = content;
        m.sentAt = sentAt;
        return m;
    }
}
