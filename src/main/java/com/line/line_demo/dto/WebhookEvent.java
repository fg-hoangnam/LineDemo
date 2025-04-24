package com.line.line_demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WebhookEvent {
    private String destination;
    private List<Event> events;
}

