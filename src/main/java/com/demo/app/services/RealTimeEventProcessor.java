package com.demo.app.services;

import com.demo.app.contracts.EventProcessor;
import com.demo.app.contracts.MetricsStore;
import com.demo.app.models.UserEvent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RealTimeEventProcessor implements EventProcessor {

    MetricsStore metricsStore;

    @Override
    public void process(UserEvent event) {

        metricsStore.incrementPageView(event.getPageUrl(), event.getTimestamp());
        metricsStore.updateActiveUser(event.getUserId(), event.getTimestamp());
        metricsStore.updateSession(event.getUserId(), event.getSessionId(), event.getTimestamp());


    }
}
