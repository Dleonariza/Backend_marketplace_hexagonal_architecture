package com.marketplace.hexagonal.Infrastructura.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.Instant;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/auth/login";
    private static final int MAX_FAILURES = 5;
    private static final int WINDOW_MINUTES = 15;
    private static final Duration WINDOW = Duration.ofMinutes(WINDOW_MINUTES);

    private final ConcurrentHashMap<String, Deque<Instant>> failedAttempts =
            new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {
        if (!isLoginRequest(request)) {
            chain.doFilter(request, response);
            return;
        }

        String ip = resolveClientIp(request);

        if (isBlocked(ip)) {
            log.warn("Rate limit exceeded for IP {} on {}", ip, LOGIN_PATH);
            sendTooManyRequests(response);
            return;
        }

        ContentCachingResponseWrapper wrappedResponse =
                new ContentCachingResponseWrapper(response);

        chain.doFilter(request, wrappedResponse);

        int status = wrappedResponse.getStatus();
        if (status >= 400 && status < 500) {
            recordFailure(ip);
        } else if (status >= 200 && status < 300) {
            clearFailures(ip);
        }

        wrappedResponse.copyBodyToResponse();
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        return HttpMethod.POST.matches(request.getMethod())
                && LOGIN_PATH.equals(request.getServletPath());
    }

    private boolean isBlocked(String ip) {
        Deque<Instant> timestamps = failedAttempts.get(ip);
        if (timestamps == null) return false;
        Instant cutoff = Instant.now().minus(WINDOW);
        synchronized (timestamps) {
            while (!timestamps.isEmpty() && timestamps.peekFirst().isBefore(cutoff)) {
                timestamps.pollFirst();
            }
            return timestamps.size() >= MAX_FAILURES;
        }
    }

    private void recordFailure(String ip) {
        failedAttempts
                .computeIfAbsent(ip, k -> new ArrayDeque<>())
                .addLast(Instant.now());
        long count = failedAttempts.get(ip).size();
        log.debug("Failed login attempt #{} for IP {}", count, ip);
        if (count >= MAX_FAILURES) {
            log.warn("IP {} has reached {} failed login attempts - blocking for {} minutes",
                    ip, MAX_FAILURES, WINDOW_MINUTES);
        }
    }

    private void clearFailures(String ip) {
        failedAttempts.remove(ip);
    }

    private void sendTooManyRequests(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                "{\"status\":429,\"error\":\"Too Many Requests\"," +
                "\"message\":\"Too many failed login attempts. Try again in " +
                WINDOW_MINUTES + " minutes.\"}");
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
