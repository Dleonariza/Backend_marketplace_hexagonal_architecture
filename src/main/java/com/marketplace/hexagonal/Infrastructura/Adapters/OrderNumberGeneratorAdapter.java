package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.Order.Ports.Out.OrderNumberGeneratorPort;
import com.marketplace.hexagonal.Domain.Order.Ports.Out.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class OrderNumberGeneratorAdapter implements OrderNumberGeneratorPort {

    static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    static final int MAX_RETRIES = 10;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final SecureRandom RANDOM = new SecureRandom();

    private final OrderRepositoryPort orderRepo;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public String next() {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            String candidate = generateCandidate();
            if (!existsInDb(candidate)) return candidate;
        }
        throw new IllegalStateException(
                "Could not generate a unique order number after " + MAX_RETRIES + " attempts");
    }

    static String generateCandidate() {
        String date = LocalDate.now().format(DATE_FMT);
        String sellerPart = "S" + randomChars(5);
        String uniquePart = randomChars(6);
        return "ORD-" + date + "-" + sellerPart + "-" + uniquePart;
    }

    static String randomChars(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    private boolean existsInDb(String numOrder) {
        return orderRepo.existsByNumOrder(numOrder);
    }
}
