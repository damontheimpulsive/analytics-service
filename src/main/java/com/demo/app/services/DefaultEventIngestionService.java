package com.demo.app.services;

import com.demo.app.contracts.EventIngestionService;
import com.demo.app.contracts.EventProcessor;
import com.demo.app.models.UserEvent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class DefaultEventIngestionService implements EventIngestionService {

    EventProcessor eventProcessor;
    @Override
    public void ingest(UserEvent event) {

        eventProcessor.process(event);

    }
}
