package com.marketplace.hexagonal.Infrastructura.Jobs;

import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryThreadRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InquiryExpirationJobTest {

    @Mock InquiryThreadRepositoryPort inquiryThreadRepo;
    InquiryExpirationJob job;

    @BeforeEach
    void setUp() throws Exception {
        job = new InquiryExpirationJob(inquiryThreadRepo);
        Field field = InquiryExpirationJob.class.getDeclaredField("inactivityMinutes");
        field.setAccessible(true);
        field.set(job, 1440);
    }

    @Test
    void expireInactiveThreads_deletesThreadsOlderThanCutoff() {
        when(inquiryThreadRepo.deleteInactiveThreads(any())).thenReturn(2);

        job.expireInactiveThreads();

        ArgumentCaptor<Instant> cutoffCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(inquiryThreadRepo).deleteInactiveThreads(cutoffCaptor.capture());

        Instant expectedCutoff = Instant.now().minus(1440, ChronoUnit.MINUTES);
        assertThat(cutoffCaptor.getValue()).isCloseTo(expectedCutoff, within(5, java.time.temporal.ChronoUnit.SECONDS));
    }

    @Test
    void expireInactiveThreads_repositoryThrows_exceptionIsSwallowed() {
        when(inquiryThreadRepo.deleteInactiveThreads(any())).thenThrow(new RuntimeException("db down"));

        assertThatCode(() -> job.expireInactiveThreads()).doesNotThrowAnyException();
    }
}
