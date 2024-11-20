package com.coursemanagement.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.TimeZone;

@Component
public class TimezoneConfig {

    @PostConstruct
    public void init() {
        // Set default timezone to UTC
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
