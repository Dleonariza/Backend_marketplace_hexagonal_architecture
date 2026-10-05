package com.marketplace.hexagonal.Infrastructura.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LoginRateLimitFilter")
class LoginRateLimitFilterTest {

    private static final String CLIENT_IP = "192.168.1.10";
    private static final String LOGIN_PATH = "/auth/login";

    private LoginRateLimitFilter filter;

    @BeforeEach
    void setUp() {
        filter = new LoginRateLimitFilter();
    }

    private MockHttpServletRequest loginRequest(String ip) {
        MockHttpServletRequest req = new MockHttpServletRequest("POST", LOGIN_PATH);
        req.setServletPath(LOGIN_PATH);
        req.setRemoteAddr(ip);
        return req;
    }

    private FilterChain chainWithStatus(int status) {
        return (req, res) -> ((HttpServletResponse) res).setStatus(status);
    }

    private MockHttpServletResponse runFilter(MockHttpServletRequest req,
                                              FilterChain chain) throws ServletException, IOException {
        MockHttpServletResponse res = new MockHttpServletResponse();
        filter.doFilter(req, res, chain);
        return res;
    }

    @SuppressWarnings("unchecked")
    private ConcurrentHashMap<String, Deque<Instant>> failedAttempts() {
        return (ConcurrentHashMap<String, Deque<Instant>>)
                ReflectionTestUtils.getField(filter, "failedAttempts");
    }

    private void injectAttempts(String ip, int count, long minutesAgo) {
        Deque<Instant> deque = new ArrayDeque<>();
        Instant ts = Instant.now().minusSeconds(minutesAgo * 60);
        for (int i = 0; i < count; i++) deque.addLast(ts);
        failedAttempts().put(ip, deque);
    }

    @Test
    @DisplayName("GET /auth/login should pass through without filtering")
    void getRequest_isNotFiltered() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", LOGIN_PATH);
        req.setServletPath(LOGIN_PATH);
        req.setRemoteAddr(CLIENT_IP);

        MockHttpServletResponse res = runFilter(req, chainWithStatus(200));

        assertThat(res.getStatus()).isEqualTo(200);
        assertThat(failedAttempts()).doesNotContainKey(CLIENT_IP);
    }

    @Test
    @DisplayName("POST to a different path passes through without filtering")
    void postToOtherPath_isNotFiltered() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/products");
        req.setServletPath("/api/products");
        req.setRemoteAddr(CLIENT_IP);

        MockHttpServletResponse res = runFilter(req, chainWithStatus(201));

        assertThat(res.getStatus()).isEqualTo(201);
        assertThat(failedAttempts()).doesNotContainKey(CLIENT_IP);
    }

    @Test
    @DisplayName("Successful login (200) does not record a failure")
    void successfulLogin_doesNotRecordFailure() throws Exception {
        MockHttpServletResponse res = runFilter(loginRequest(CLIENT_IP), chainWithStatus(200));

        assertThat(res.getStatus()).isEqualTo(200);
        assertThat(failedAttempts()).doesNotContainKey(CLIENT_IP);
    }

    @Test
    @DisplayName("Successful login clears previous failures for that IP")
    void successfulLogin_clearsPreviousFailures() throws Exception {
        injectAttempts(CLIENT_IP, 3, 0);
        assertThat(failedAttempts()).containsKey(CLIENT_IP);

        runFilter(loginRequest(CLIENT_IP), chainWithStatus(200));

        assertThat(failedAttempts()).doesNotContainKey(CLIENT_IP);
    }

    @Test
    @DisplayName("Failed login (401) records one failure for the IP")
    void failedLogin_recordsOneFailure() throws Exception {
        runFilter(loginRequest(CLIENT_IP), chainWithStatus(401));

        assertThat(failedAttempts()).containsKey(CLIENT_IP);
        assertThat(failedAttempts().get(CLIENT_IP)).hasSize(1);
    }

    @Test
    @DisplayName("Failed login (403) also counts as a failure")
    void failedLogin_403_recordsFailure() throws Exception {
        runFilter(loginRequest(CLIENT_IP), chainWithStatus(403));

        assertThat(failedAttempts().get(CLIENT_IP)).hasSize(1);
    }

    @Test
    @DisplayName("Multiple failures accumulate for the same IP")
    void multipleFailures_accumulate() throws Exception {
        FilterChain badCredentials = chainWithStatus(401);

        runFilter(loginRequest(CLIENT_IP), badCredentials);
        runFilter(loginRequest(CLIENT_IP), badCredentials);
        runFilter(loginRequest(CLIENT_IP), badCredentials);

        assertThat(failedAttempts().get(CLIENT_IP)).hasSize(3);
    }

    @Test
    @DisplayName("5xx downstream response is not counted as a failure")
    void serverError_doesNotRecordFailure() throws Exception {
        runFilter(loginRequest(CLIENT_IP), chainWithStatus(500));

        assertThat(failedAttempts()).doesNotContainKey(CLIENT_IP);
    }

    @Test
    @DisplayName("After 5 failures the next request receives HTTP 429")
    void afterFiveFailures_returns429() throws Exception {
        FilterChain badCredentials = chainWithStatus(401);

        for (int i = 0; i < 5; i++) {
            runFilter(loginRequest(CLIENT_IP), badCredentials);
        }

        FilterChain shouldNotBeCalled = (req, res) -> {
            throw new AssertionError("Chain must not be invoked when IP is blocked");
        };
        MockHttpServletResponse blocked = runFilter(loginRequest(CLIENT_IP), shouldNotBeCalled);

        assertThat(blocked.getStatus()).isEqualTo(429);
        assertThat(blocked.getContentType()).isEqualTo("application/json");
        assertThat(blocked.getContentAsString()).contains("429");
        assertThat(blocked.getContentAsString()).contains("Too Many Requests");
    }

    @Test
    @DisplayName("Exactly 4 failures do not trigger blocking")
    void fourFailures_notBlocked() throws Exception {
        FilterChain badCredentials = chainWithStatus(401);
        for (int i = 0; i < 4; i++) {
            runFilter(loginRequest(CLIENT_IP), badCredentials);
        }

        MockHttpServletResponse res = runFilter(loginRequest(CLIENT_IP), chainWithStatus(401));

        assertThat(res.getStatus()).isEqualTo(401);
    }

    @Test
    @DisplayName("Different IPs have independent counters")
    void differentIps_haveIndependentCounters() throws Exception {
        String ip2 = "10.0.0.2";
        FilterChain badCredentials = chainWithStatus(401);

        for (int i = 0; i < 5; i++) {
            runFilter(loginRequest(CLIENT_IP), badCredentials);
        }

        MockHttpServletResponse res = runFilter(loginRequest(ip2), chainWithStatus(200));
        assertThat(res.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("Failures older than 15 minutes are evicted and do not count")
    void oldFailures_areEvicted_andDoNotBlock() throws Exception {
        injectAttempts(CLIENT_IP, 5, 16);

        MockHttpServletResponse res = runFilter(loginRequest(CLIENT_IP), chainWithStatus(401));
        assertThat(res.getStatus()).isEqualTo(401);
    }

    @Test
    @DisplayName("Mix of in-window and out-of-window failures: only in-window entries count")
    void mixedTimestamps_onlyInWindowEntriesCount() throws Exception {
        injectAttempts(CLIENT_IP, 4, 20);
        Deque<Instant> deque = failedAttempts().get(CLIENT_IP);
        deque.addLast(Instant.now());

        MockHttpServletResponse res = runFilter(loginRequest(CLIENT_IP), chainWithStatus(401));
        assertThat(res.getStatus()).isEqualTo(401);
    }

    @Test
    @DisplayName("X-Forwarded-For header is used as the client IP")
    void xForwardedFor_usedAsClientIp() throws Exception {
        String realIp = "203.0.113.5";
        String proxyIp = "10.0.0.1";

        MockHttpServletRequest req = new MockHttpServletRequest("POST", LOGIN_PATH);
        req.setServletPath(LOGIN_PATH);
        req.setRemoteAddr(proxyIp);
        req.addHeader("X-Forwarded-For", realIp);

        FilterChain badCredentials = chainWithStatus(401);
        runFilter(req, badCredentials);

        assertThat(failedAttempts()).containsKey(realIp);
        assertThat(failedAttempts()).doesNotContainKey(proxyIp);
    }

    @Test
    @DisplayName("X-Forwarded-For with multiple proxies uses the first (leftmost) IP")
    void xForwardedFor_multipleProxies_usesFirst() throws Exception {
        String realIp = "203.0.113.5";
        String chain = realIp + ", 10.0.0.1, 10.0.0.2";

        MockHttpServletRequest req = new MockHttpServletRequest("POST", LOGIN_PATH);
        req.setServletPath(LOGIN_PATH);
        req.setRemoteAddr("10.0.0.2");
        req.addHeader("X-Forwarded-For", chain);

        runFilter(req, chainWithStatus(401));

        assertThat(failedAttempts()).containsKey(realIp);
    }

    @Test
    @DisplayName("Falls back to remoteAddr when X-Forwarded-For is absent")
    void noXForwardedFor_usesRemoteAddr() throws Exception {
        runFilter(loginRequest(CLIENT_IP), chainWithStatus(401));

        assertThat(failedAttempts()).containsKey(CLIENT_IP);
    }

    @Test
    @DisplayName("Blank X-Forwarded-For falls back to remoteAddr")
    void blankXForwardedFor_fallsBackToRemoteAddr() throws Exception {
        MockHttpServletRequest req = loginRequest(CLIENT_IP);
        req.addHeader("X-Forwarded-For", "   ");

        runFilter(req, chainWithStatus(401));

        assertThat(failedAttempts()).containsKey(CLIENT_IP);
    }
}
