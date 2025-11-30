package com.demo.app.services;

import com.demo.app.contracts.EventIngestionService;
import com.demo.app.contracts.MockEventGenerator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class MockEventGeneratorRunner {


    private final EventIngestionService eventIngestionService;
    private MockEventGenerator mockEventGenerator;
    private final ScheduledExecutorService controlScheduler =
            Executors.newSingleThreadScheduledExecutor();


    public MockEventGeneratorRunner(EventIngestionService eventIngestionService) {
        this.eventIngestionService = eventIngestionService;
    }

    @PostConstruct
    public void startGenerator() {
        System.out.println("Starting continuous mock event generator...");
        this.mockEventGenerator = new DefaultMockEventGenerator(eventIngestionService, 50L); // ~20 events/sec
        this.mockEventGenerator.start();

        // Toggle pause/resume every 5 seconds
        controlScheduler.scheduleAtFixedRate(new Runnable() {
            private boolean paused = false;

            @Override
            public void run() {
                if (mockEventGenerator == null) {
                    return;
                }
                if (paused) {
                    mockEventGenerator.resume();
                } else {
                    mockEventGenerator.pause();
                }
                paused = !paused;
            }
        }, 5, 5, TimeUnit.SECONDS);

    }

    @PreDestroy
    public void stopGenerator() {
        System.out.println("Stopping mock event generator...");
        if (this.mockEventGenerator != null) {
            this.mockEventGenerator.stop();
        }
        controlScheduler.shutdownNow();

    }

}
