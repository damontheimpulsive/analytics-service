package com.demo.app.contracts;

import com.demo.app.models.UserEvent;

public interface EventProcessor {

    void process(UserEvent event);
}
