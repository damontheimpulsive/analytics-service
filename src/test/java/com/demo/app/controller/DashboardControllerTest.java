package com.demo.app.controller;

import com.demo.app.contracts.DashboardService;
import com.demo.app.contracts.EventIngestionService;
import com.demo.app.models.ActiveUserMetric;
import com.demo.app.models.PageViewMetric;
import com.demo.app.models.UserSessionMetric;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class DashboardControllerTest {

    private DashboardService dashboardService;
    private EventIngestionService eventIngestionService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        dashboardService = mock(DashboardService.class);
        eventIngestionService = mock(EventIngestionService.class);

        DashboardController controller = new DashboardController(dashboardService, eventIngestionService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void dashboardHome_shouldReturnEventsLoaded() throws Exception {

        mockMvc.perform(get("/dashboard"))
               .andExpect(status().isOk())
               .andExpect(content().string("Events Loaded"));
        // No direct interaction expectations on services here because
        // DashboardController builds DefaultMockEventGenerator internally.
    }

    @Test
    void getActiveUsers_shouldReturnMetricFromService() throws Exception {

        ActiveUserMetric metric = new ActiveUserMetric(5L);
        when(dashboardService.getActiveUsers()).thenReturn(metric);

        mockMvc.perform(get("/dashboard/active-users")
                        .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.count", is(5)));

        verify(dashboardService).getActiveUsers();
        verifyNoMoreInteractions(dashboardService);
    }

    @Test
    void getTopPages_shouldReturnListFromService() throws Exception {
        List<PageViewMetric> metrics = Arrays.asList(
                new PageViewMetric("/a", 10),
                new PageViewMetric("/b", 5)
        );
        when(dashboardService.getTopPages()).thenReturn(metrics);

        mockMvc.perform(get("/dashboard/top-pages")
                        .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].url", is("/a")))
               .andExpect(jsonPath("$[0].count", is(10)))
               .andExpect(jsonPath("$[1].url", is("/b")))
               .andExpect(jsonPath("$[1].count", is(5)));

        verify(dashboardService).getTopPages();
        verifyNoMoreInteractions(dashboardService);
    }

    @Test
    void getActiveSessions_shouldReturnMetricFromService() throws Exception {
        String userId = "user-123";
        UserSessionMetric metric = new UserSessionMetric(userId, 3L);
        when(dashboardService.getActiveSessions(eq(userId))).thenReturn(metric);

        mockMvc.perform(get("/dashboard/users/{userId}/active-sessions", userId)
                        .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.userId", is(userId)))
               .andExpect(jsonPath("$.activeSessions", is(3)));

        verify(dashboardService).getActiveSessions(userId);
        verifyNoMoreInteractions(dashboardService);
    }
}
