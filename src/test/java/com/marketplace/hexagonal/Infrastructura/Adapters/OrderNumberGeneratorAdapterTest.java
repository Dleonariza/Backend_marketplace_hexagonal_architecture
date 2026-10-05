package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.Order.Ports.Out.OrderRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import static com.marketplace.hexagonal.Infrastructura.Adapters.OrderNumberGeneratorAdapter.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("OrderNumberGeneratorAdapter")
@ExtendWith(MockitoExtension.class)
class OrderNumberGeneratorAdapterTest {

    @Mock OrderRepositoryPort orderRepo;
    OrderNumberGeneratorAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new OrderNumberGeneratorAdapter(orderRepo);
    }

    @Test
    @DisplayName("generateCandidate() matches ORD-YYYYMMDD-SXXXXX-XXXXXX pattern")
    void generateCandidate_matchesExpectedPattern() {
        String candidate = generateCandidate();

        assertThat(candidate).matches("ORD-\\d{8}-S[A-Z0-9]{5}-[A-Z0-9]{6}");
    }

    @Test
    @DisplayName("generateCandidate() is exactly 26 characters")
    void generateCandidate_isExactly26Chars() {
        assertThat(generateCandidate()).hasSize(26);
    }

    @Test
    @DisplayName("generateCandidate() date segment matches today")
    void generateCandidate_dateSegmentIsToday() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String candidate = generateCandidate();
        assertThat(candidate).startsWith("ORD-" + today + "-");
    }

    @Test
    @DisplayName("generateCandidate() seller segment always starts with 'S'")
    void generateCandidate_sellerSegmentStartsWithS() {
        String candidate = generateCandidate();
        String[] parts = candidate.split("-");
        assertThat(parts[2]).startsWith("S");
        assertThat(parts[2]).hasSize(6);
    }

    @Test
    @DisplayName("generateCandidate() unique segment is 6 alphanumeric chars")
    void generateCandidate_uniqueSegmentIs6AlphanumericChars() {
        String candidate = generateCandidate();
        String[] parts = candidate.split("-");
        assertThat(parts[3]).matches("[A-Z0-9]{6}");
    }

    @Test
    @DisplayName("randomChars() returns the requested length")
    void randomChars_returnsRequestedLength() {
        assertThat(randomChars(5)).hasSize(5);
        assertThat(randomChars(6)).hasSize(6);
    }

    @Test
    @DisplayName("randomChars() only uses characters from the allowed alphabet")
    void randomChars_usesOnlyAllowedChars() {
        for (int i = 0; i < 200; i++) {
            assertThat(randomChars(10)).matches("[A-Z0-9]+");
        }
    }

    @RepeatedTest(10)
    @DisplayName("randomChars() produces different values across calls (statistically)")
    void randomChars_isDifferentAcrossCalls() {
        Set<String> samples = new HashSet<>();
        for (int i = 0; i < 20; i++) samples.add(randomChars(6));

        assertThat(samples.size()).isGreaterThan(1);
    }

    @Test
    @DisplayName("next() returns a candidate when no collision")
    void next_noCollision_returnsCandidateImmediately() {
        when(orderRepo.existsByNumOrder(anyString())).thenReturn(false);

        String result = adapter.next();

        assertThat(result).matches("ORD-\\d{8}-S[A-Z0-9]{5}-[A-Z0-9]{6}");
        verify(orderRepo, times(1)).existsByNumOrder(anyString());
    }

    @Test
    @DisplayName("next() retries on collision and returns the first non-colliding candidate")
    void next_oneCollision_retriesAndSucceeds() {
        when(orderRepo.existsByNumOrder(anyString()))
                .thenReturn(true)
                .thenReturn(false);

        String result = adapter.next();

        assertThat(result).matches("ORD-\\d{8}-S[A-Z0-9]{5}-[A-Z0-9]{6}");
        verify(orderRepo, times(2)).existsByNumOrder(anyString());
    }

    @Test
    @DisplayName("next() throws after MAX_RETRIES consecutive collisions")
    void next_maxRetriesExhausted_throwsIllegalStateException() {
        when(orderRepo.existsByNumOrder(anyString())).thenReturn(true);

        assertThatThrownBy(() -> adapter.next())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.valueOf(MAX_RETRIES));

        verify(orderRepo, times(MAX_RETRIES)).existsByNumOrder(anyString());
    }

    @Test
    @DisplayName("1000 generated candidates are all unique")
    void generateCandidate_1000samples_areAllUnique() {
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            seen.add(generateCandidate());
        }

        assertThat(seen).hasSize(1000);
    }
}
