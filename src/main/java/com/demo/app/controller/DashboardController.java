package com.demo.app.controller;


import com.demo.app.contracts.DashboardService;
import com.demo.app.contracts.EventIngestionService;
import com.demo.app.models.ActiveUserMetric;
import com.demo.app.models.PageViewMetric;
import com.demo.app.models.UserSessionMetric;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    DashboardService dashboardService;
    EventIngestionService eventIngestionService;

    public DashboardController(DashboardService dashboardService,
                               EventIngestionService eventIngestionService) throws InterruptedException {
        this.dashboardService = dashboardService;
        this.eventIngestionService = eventIngestionService;
    }


//    private void dashboardHome() throws InterruptedException {
//
//        System.out.println("Load Events");
//
//        MockEventGenerator mockEventGenerator = new DefaultMockEventGenerator(eventIngestionService, 50L); // 20 events/sec
//        mockEventGenerator.start();
//
//        // Let it run for 10 seconds
//        Thread.sleep(10000);
//        mockEventGenerator.stop();
//
//        System.out.println("Events Loaded:");
//
//    }

    // 1. Active users - last 5 minutes
    @GetMapping("/active-users")
    public ActiveUserMetric getActiveUsers() {
        return dashboardService.getActiveUsers();
    }

    // 2. Top 5 pages (last 15 minutes)
    @GetMapping("/top-pages")
    public List<PageViewMetric> getTopPages() {
        return dashboardService.getTopPages();
    }

    // 3. Active sessions for a user
    @GetMapping("/users/{userId}/active-sessions")
    public UserSessionMetric getActiveSessions(@PathVariable String userId) {
        return dashboardService.getActiveSessions(userId);
    }
}
