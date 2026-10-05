package com.marketplace.hexagonal.Domain.Inquiry.Ports.Out;

import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryMessage;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThreadId;

import java.util.List;

public interface InquiryMessageRepositoryPort {

    InquiryMessage save(InquiryMessage message);

    List<InquiryMessage> findByThreadId(InquiryThreadId threadId, int page, int size);

    long countByThreadId(InquiryThreadId threadId);

    List<InquiryMessage> findAllByThreadIdOrderBySentAtAsc(InquiryThreadId threadId);
}
