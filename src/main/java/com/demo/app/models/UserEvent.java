package com.demo.app.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@AllArgsConstructor
@Data
public class UserEvent {

    private String userId;
    private EventType eventType;
    private String pageUrl;
    private String sessionId;
    private Instant timestamp;
}
