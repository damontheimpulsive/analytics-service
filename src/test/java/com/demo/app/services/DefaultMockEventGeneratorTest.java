package com.demo.app.services;

import com.demo.app.contracts.EventIngestionService;
import com.demo.app.models.UserEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


public class DefaultMockEventGeneratorTest {

    private final EventIngestionService ingestionService = mock(EventIngestionService.class);

    // use a small interval so the test does not take long
    private final long intervalMs = 50L;

    private DefaultMockEventGenerator generator;

    @AfterEach
    void tearDown() {
        if (generator != null) {
            generator.stop();
        }
    }

    @Test
    void start_shouldPeriodicallyIngestEvents() throws Exception {

        generator = new DefaultMockEventGenerator(ingestionService, intervalMs);

        generator.start();

        // Wait a bit longer than one interval to give scheduler time to run
        TimeUnit.MILLISECONDS.sleep(intervalMs * 4);

        // Verify that ingest was called at least once
        ArgumentCaptor<UserEvent> eventCaptor = ArgumentCaptor.forClass(UserEvent.class);
        verify(ingestionService, atLeastOnce()).ingest(eventCaptor.capture());

        UserEvent firstEvent = eventCaptor.getAllValues().get(0);
        assertNotNull(firstEvent, "Generated event should not be null");
        assertNotNull(firstEvent.getUserId(), "UserId should not be null");
        assertNotNull(firstEvent.getEventType(), "EventType should not be null");
        assertNotNull(firstEvent.getTimestamp(), "Timestamp should not be null");
    }

    @Test
    void stop_shouldNotThrow() {


        generator = new DefaultMockEventGenerator(ingestionService, intervalMs);

        generator.start();

        // If stop throws, test will fail
        generator.stop();
    }
}