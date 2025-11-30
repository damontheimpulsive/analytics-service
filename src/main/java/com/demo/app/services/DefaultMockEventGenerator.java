package com.demo.app.services;

import com.demo.app.contracts.EventIngestionService;
import com.demo.app.contracts.MockEventGenerator;
import com.demo.app.models.EventType;
import com.demo.app.models.UserEvent;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultMockEventGenerator implements MockEventGenerator {

    private final EventIngestionService ingestionService;
    // Control interval between events (in milliseconds)
    long intervalMs;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Random random = new Random();
    private final AtomicBoolean paused = new AtomicBoolean(false);


    public DefaultMockEventGenerator(EventIngestionService ingestionService, long intervalMs) {
        this.ingestionService = ingestionService;
        this.intervalMs = intervalMs;
    }

    // Sample data pool
    private final List<String> pages = List.of(
            "/home",
            "/products/electronics",
            "/products/fashion",
            "/cart",
            "/checkout"
    );

    private final List<EventType> eventTypes = List.of(EventType.PAGE_VIEW, EventType.CLICK);


    @Override
    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
               if(paused.get()){
                   return;
               }
                UserEvent event = generateRandomEvent();
                ingestionService.ingest(event);
                System.out.println("Generated event: " + event);
            } catch (Exception ex) {
                System.err.println("Failed to generate/ingest event: " + ex.getMessage());
            }
        }, 0, intervalMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        scheduler.shutdownNow();
        System.out.println("Event generator stopped.");
    }
    @Override
    public void pause() {
        paused.set(true);
        System.out.println("Event generator paused.");
    }
    @Override
    public void resume() {
        paused.set(false);
        System.out.println("Event generator resumed.");
    }


    private UserEvent generateRandomEvent() {

        String userId = "usr_" + random.nextInt(100);  // 100 users pool
        String sessionId = "sess_" + random.nextInt(50); // 50 sessions pool for demo

        return new UserEvent(
                userId,
                eventTypes.get(random.nextInt(eventTypes.size())),
                pages.get(random.nextInt(pages.size())),
                sessionId,
                Instant.now()
        );
    }
}
