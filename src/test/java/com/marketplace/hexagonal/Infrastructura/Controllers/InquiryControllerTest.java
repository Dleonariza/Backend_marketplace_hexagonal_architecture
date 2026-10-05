package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Inquiry.Services.InquiryService;
import com.marketplace.hexagonal.Domain.Inquiry.Exceptions.InquiryThreadAccessDeniedException;
import com.marketplace.hexagonal.Domain.Inquiry.Exceptions.InquiryThreadNotFoundException;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryStatus;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThread;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThreadId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Exception.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class InquiryControllerTest {

    @Mock InquiryService inquiryService;
    @Mock UserRepositoryPort userRepo;
    @InjectMocks InquiryController controller;
    MockMvc mockMvc;

    private static final String USER_EMAIL = "buyer@test.com";
    private static final UserId USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final UserId SELLER_UID = UserId.of("bc66d237-f54d-4bad-b521-0eba3b06304c");
    private static final ProductId PRODUCT_ID = ProductId.of("aa000000-0000-0000-0000-000000000001");
    private static final InquiryThreadId THREAD_ID = InquiryThreadId.generate();

    @BeforeEach
    void setUp() {
        var auth = new UsernamePasswordAuthenticationToken(
                USER_EMAIL, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        UserModel user = UserModel.create(USER_EMAIL, "buyer1", "hash", "Juan", null, "3001234567", List.of());
        lenient().when(userRepo.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .addFilter((req, res, chain) -> chain.doFilter(
                        new HttpServletRequestWrapper((HttpServletRequest) req) {
                            @Override public java.security.Principal getUserPrincipal() {
                                Authentication a = SecurityContextHolder.getContext().getAuthentication();
                                return a;
                            }
                        }, res))
                .build();
    }

    @AfterEach
    void tearDown() { SecurityContextHolder.clearContext(); }

    @Test
    void getOrCreateThread_valid_returns200() throws Exception {
        InquiryThread thread = buildThread();
        when(inquiryService.getOrCreateThread(any(), eq(PRODUCT_ID.getValue()))).thenReturn(thread);

        mockMvc.perform(post("/products/" + PRODUCT_ID.getValue() + "/inquiries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.threadId").value(THREAD_ID.getValue()))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void listThreads_returnsThreadsForCaller() throws Exception {
        when(inquiryService.getUserThreads(any())).thenReturn(List.of(buildThread()));

        mockMvc.perform(get("/inquiries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getMessages_returnsPage() throws Exception {
        when(inquiryService.getMessages(eq(THREAD_ID), any(), eq(0), eq(20))).thenReturn(List.of());
        when(inquiryService.countMessages(eq(THREAD_ID), any())).thenReturn(0L);

        mockMvc.perform(get("/inquiries/" + THREAD_ID.getValue() + "/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMessages").value(0));
    }

    @Test
    void getMessages_notFound_returns404() throws Exception {
        when(inquiryService.getMessages(eq(THREAD_ID), any(), anyInt(), anyInt()))
                .thenThrow(new InquiryThreadNotFoundException(THREAD_ID.getValue()));

        mockMvc.perform(get("/inquiries/" + THREAD_ID.getValue() + "/messages"))
                .andExpect(status().isNotFound());
    }

    @Test
    void closeThread_notParticipant_returns403() throws Exception {
        when(inquiryService.closeThread(eq(THREAD_ID), any()))
                .thenThrow(new InquiryThreadAccessDeniedException());

        mockMvc.perform(post("/inquiries/" + THREAD_ID.getValue() + "/close"))
                .andExpect(status().isForbidden());
    }

    private InquiryThread buildThread() {
        return InquiryThread.reconstitute(
                THREAD_ID, PRODUCT_ID, USER_ID, SELLER_UID,
                InquiryStatus.OPEN, Instant.now(), Instant.now());
    }
}
