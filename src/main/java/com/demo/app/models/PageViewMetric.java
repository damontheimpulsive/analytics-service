package com.demo.app.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageViewMetric {

    private String url;
    private long count;
}
