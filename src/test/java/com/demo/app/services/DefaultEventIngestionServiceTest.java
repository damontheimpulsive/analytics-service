package com.demo.app.services;

import com.demo.app.contracts.EventProcessor;
import com.demo.app.models.EventType;
import com.demo.app.models.UserEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.mockito.Mockito.*;

public class DefaultEventIngestionServiceTest {

    private EventProcessor eventProcessor;
    private DefaultEventIngestionService ingestionService;

    @BeforeEach
    void setUp() {
        eventProcessor = mock(EventProcessor.class);
        ingestionService = new DefaultEventIngestionService(eventProcessor);
    }

    @Test
    void ingest_shouldDelegateToEventProcessor() {

        String userId = "user-1";
        String sessionId = "session-xyz";
        String pageUrl = "/home";
        Instant timestamp = Instant.now();

        UserEvent event = new UserEvent(userId, EventType.CLICK, pageUrl, sessionId, timestamp);

        ingestionService.ingest(event);

        verify(eventProcessor).process(event);
        verifyNoMoreInteractions(eventProcessor);
    }
}
