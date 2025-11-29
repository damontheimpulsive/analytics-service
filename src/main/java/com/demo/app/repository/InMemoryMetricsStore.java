package com.demo.app.repository;

import com.demo.app.contracts.MetricsStore;
import com.demo.app.models.PageViewMetric;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@NoArgsConstructor
@Repository
public class InMemoryMetricsStore implements MetricsStore {


    // Tracks last activity time for each user
    private final Map<String, Instant> activeUsers = new ConcurrentHashMap<>();

    // Tracks timestamps for each page view
    private final Map<String, List<Instant>> pageViews = new ConcurrentHashMap<>();

    // Tracks sessions per user
    private final Map<String, Map<String, Instant>> userSessions = new ConcurrentHashMap<>();

    @Override
    public void incrementPageView(String pageUrl, Instant timestamp) {
        pageViews.computeIfAbsent(pageUrl, k -> Collections.synchronizedList(new ArrayList<>())).add(timestamp);
    }

    @Override
    public void updateActiveUser(String userId, Instant timestamp) {
        activeUsers.put(userId, timestamp);
    }

    @Override
    public void updateSession(String userId, String sessionId, Instant timestamp) {

        userSessions
                .computeIfAbsent(userId, k -> new ConcurrentHashMap<>())
                .put(sessionId, timestamp);
    }

    @Override
    public long getActiveUserCount(Duration window) {
        Instant cutoff = Instant.now().minus(window);
        activeUsers.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoff));
        return activeUsers.size();
    }

    @Override
    public List<PageViewMetric> getTopPages(Duration window, int limit) {


        Instant cutoff = Instant.now().minus(window);

        return pageViews.entrySet()
                .stream()
                .map(entry -> toPageViewMetricAfterCleanup(entry, cutoff))
                .sorted(Comparator.comparingLong(PageViewMetric::getCount).reversed())
                .limit(limit)
                .collect(Collectors.toList());

    }

    @Override
    public long getActiveSessions(String userId, Duration window) {
        Instant cutoff = Instant.now().minus(window);

        Map<String, Instant> sessions = userSessions.get(userId);
        if (sessions == null) return 0;

        sessions.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoff));
        return sessions.size();
    }

    private PageViewMetric toPageViewMetricAfterCleanup(Map.Entry<String, List<Instant>> entry,
                                                        Instant cutoff) {

        var url = entry.getKey();
        var timestamps = entry.getValue();

        // Remove outdated timestamps in-place
        timestamps.removeIf(ts -> ts.isBefore(cutoff));

        var count = timestamps.size();
        return new PageViewMetric(url, count);
    }

}
