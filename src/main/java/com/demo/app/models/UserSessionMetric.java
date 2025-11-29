package com.demo.app.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserSessionMetric {
    private String userId;
    private long activeSessions;
}
