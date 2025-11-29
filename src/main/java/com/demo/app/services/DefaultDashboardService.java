package com.demo.app.services;

import com.demo.app.contracts.DashboardService;
import com.demo.app.contracts.MetricsStore;
import com.demo.app.models.ActiveUserMetric;
import com.demo.app.models.PageViewMetric;
import com.demo.app.models.UserSessionMetric;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@AllArgsConstructor
public class DefaultDashboardService implements DashboardService {

    private final MetricsStore metricsStore;

    @Override
    public ActiveUserMetric getActiveUsers() {
        return new ActiveUserMetric(metricsStore.getActiveUserCount(Duration.ofMinutes(5)));
    }

    @Override
    public List<PageViewMetric> getTopPages() {
        return metricsStore.getTopPages(Duration.ofMinutes(15), 5);
    }

    @Override
    public UserSessionMetric getActiveSessions(String userId) {
        return new UserSessionMetric(userId, metricsStore.getActiveSessions(userId, Duration.ofMinutes(5)));
    }


}
