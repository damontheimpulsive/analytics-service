package com.demo.app.contracts;

import com.demo.app.models.PageViewMetric;

import java.time.Duration;
import java.time.Instant;
import java.util.List;


public interface MetricsStore {

    void incrementPageView(String pageUrl, Instant timestamp);

    void updateActiveUser(String userId, Instant timestamp);

    void updateSession(String userId, String sessionId, Instant timestamp);

    // Read APIs
    long getActiveUserCount(Duration window);

    List<PageViewMetric> getTopPages(Duration window, int limit);

    long getActiveSessions(String userId, Duration window);
}
