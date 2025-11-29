package com.demo.app.contracts;

import com.demo.app.models.UserEvent;

public interface EventIngestionService {

    void ingest(UserEvent event);
}
