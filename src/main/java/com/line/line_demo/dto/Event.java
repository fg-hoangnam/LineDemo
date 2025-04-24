package com.line.line_demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {
    private String type;
    private Message message;
    private Object follow;
    private String webhookEventId;
    private DeliveryContext deliveryContext;
    private long timestamp;
    private Source source;
    private String replyToken;
    private String mode;

    // getters and setters
}
