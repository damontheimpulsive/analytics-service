package com.demo.app.contracts;

import com.demo.app.models.ActiveUserMetric;
import com.demo.app.models.PageViewMetric;
import com.demo.app.models.UserSessionMetric;

import java.util.List;

public interface DashboardService {

    ActiveUserMetric getActiveUsers();

    List<PageViewMetric> getTopPages();

    UserSessionMetric getActiveSessions(String userId);
}
