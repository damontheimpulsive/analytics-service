package com.demo.app.services;

import com.demo.app.contracts.MetricsStore;
import com.demo.app.models.ActiveUserMetric;
import com.demo.app.models.PageViewMetric;
import com.demo.app.models.UserSessionMetric;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DefaultDashboardServiceTest {

    private MetricsStore metricsStore;
    private DefaultDashboardService dashboardService;

    @BeforeEach
    void setUp() {
        metricsStore = mock(MetricsStore.class);
        dashboardService = new DefaultDashboardService(metricsStore);
    }

    @Test
    void getActiveUsers_shouldReturnMetricWithCountFromStore() {
        long expectedCount = 42L;
        when(metricsStore.getActiveUserCount(ArgumentMatchers.eq(Duration.ofMinutes(5))))
                .thenReturn(expectedCount);

        ActiveUserMetric activeUserMetric = dashboardService.getActiveUsers();

        assertEquals(expectedCount, activeUserMetric.getCount());
        verify(metricsStore).getActiveUserCount(Duration.ofMinutes(5));
        verifyNoMoreInteractions(metricsStore);
    }

    @Test
    void getTopPages_shouldReturnTopPagesFromStore() {
        List<PageViewMetric> topPages = Arrays.asList(
                new PageViewMetric("/a", 10),
                new PageViewMetric("/b", 5)
        );
        when(metricsStore.getTopPages(Duration.ofMinutes(15), 5))
                .thenReturn(topPages);

        List<PageViewMetric> result = dashboardService.getTopPages();

        assertEquals(topPages, result);
        verify(metricsStore).getTopPages(Duration.ofMinutes(15), 5);
        verifyNoMoreInteractions(metricsStore);
    }

    @Test
    void getActiveSessions_shouldReturnMetricWithCountFromStore() {
        String userId = "user-123";
        long expectedSessions = 3L;

        when(metricsStore.getActiveSessions(eq(userId), eq(Duration.ofMinutes(5))))
                .thenReturn(expectedSessions);

        UserSessionMetric result = dashboardService.getActiveSessions(userId);

        assertEquals(userId, result.getUserId());
        assertEquals(expectedSessions, result.getActiveSessions());
        verify(metricsStore).getActiveSessions(userId, Duration.ofMinutes(5));
        verifyNoMoreInteractions(metricsStore);
    }
}
