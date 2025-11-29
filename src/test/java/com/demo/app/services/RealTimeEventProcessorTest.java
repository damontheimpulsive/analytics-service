package com.demo.app.services;

import com.demo.app.contracts.MetricsStore;
import com.demo.app.models.EventType;
import com.demo.app.models.UserEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.mockito.Mockito.*;

public class RealTimeEventProcessorTest {

    private MetricsStore metricsStore;
    private RealTimeEventProcessor processor;

    @BeforeEach
    void setUp() {
        metricsStore = mock(MetricsStore.class);
        processor = new RealTimeEventProcessor(metricsStore);
    }

    @Test
    void process_shouldUpdateMetricsStoreWithEventData() {

        String userId = "user-1";
        String sessionId = "session-xyz";
        String pageUrl = "/home";
        Instant timestamp = Instant.now();

        UserEvent event = new UserEvent(userId, EventType.CLICK, pageUrl, sessionId, timestamp);
        processor.process(event);

        verify(metricsStore).incrementPageView(pageUrl, timestamp);
        verify(metricsStore).updateActiveUser(userId, timestamp);
        verify(metricsStore).updateSession(userId, sessionId, timestamp);
        verifyNoMoreInteractions(metricsStore);
    }
}
