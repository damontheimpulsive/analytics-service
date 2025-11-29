package com.demo.app.repository;

import com.demo.app.models.PageViewMetric;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryMetricsStoreTest {

    private InMemoryMetricsStore metricsStore;

    @BeforeEach
    void setUp() {
        metricsStore = new InMemoryMetricsStore();
    }

    @Test
    void getActiveUserCount_shouldCountOnlyUsersWithinWindow() {
        Instant now = Instant.now();
        // within 5 minutes
        metricsStore.updateActiveUser("user1", now.minusSeconds(60));
        metricsStore.updateActiveUser("user2", now.minusSeconds(2 * 60));
        // outside 5 minutes
        metricsStore.updateActiveUser("user3", now.minusSeconds(10 * 60));

        long count = metricsStore.getActiveUserCount(Duration.ofMinutes(5));

        assertEquals(2L, count);
    }

    @Test
    void getTopPages_shouldReturnSortedByPageViewsWithinWindow() {
        Instant now = Instant.now();

        // /a: 3 views within window
        metricsStore.incrementPageView("/a", now.minusSeconds(60));
        metricsStore.incrementPageView("/a", now.minusSeconds(120));
        metricsStore.incrementPageView("/a", now.minusSeconds(180));

        // /b: 1 view within window
        metricsStore.incrementPageView("/b", now.minusSeconds(60));

        // /c: only old views (outside 15 minutes)
        metricsStore.incrementPageView("/c", now.minusSeconds(60 * 60));

        List<PageViewMetric> topPages = metricsStore.getTopPages(Duration.ofMinutes(15), 5);

        assertEquals(3, topPages.size());
        assertEquals(3L, topPages.get(0).getCount());
        assertEquals(1L, topPages.get(1).getCount());
    }

    @Test
    void getTopPages_shouldRespectLimit() {
        Instant now = Instant.now();

        metricsStore.incrementPageView("/a", now);
        metricsStore.incrementPageView("/b", now);
        metricsStore.incrementPageView("/c", now);

        List<PageViewMetric> topPages = metricsStore.getTopPages(Duration.ofMinutes(15), 2);

        assertEquals(2, topPages.size());
    }

    @Test
    void getActiveSessions_shouldCountOnlySessionsWithinWindow() {
        Instant now = Instant.now();
        String userId = "user-1";

        // sessions within window
        metricsStore.updateSession(userId, "s1", now.minusSeconds(60));
        metricsStore.updateSession(userId, "s2", now.minusSeconds(2 * 60));

        // session outside window
        metricsStore.updateSession(userId, "s3", now.minusSeconds(20 * 60));

        long count = metricsStore.getActiveSessions(userId, Duration.ofMinutes(5));

        assertEquals(2L, count);
    }

    @Test
    void getActiveSessions_shouldReturnZeroForUnknownUser() {
        long count = metricsStore.getActiveSessions("unknown-user", Duration.ofMinutes(5));
        assertEquals(0L, count);
    }
}
