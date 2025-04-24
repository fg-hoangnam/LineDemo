package com.line.line_demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
    private String type;
    private String id;
    private String quoteToken;
    private String text;

    @JsonProperty("value")
    private Integer sendLimit;
    private Integer totalUsage;

    // getters and setters

    public Message(String type, String text) {
        this.type = type;
        this.text = text;
    }
}
