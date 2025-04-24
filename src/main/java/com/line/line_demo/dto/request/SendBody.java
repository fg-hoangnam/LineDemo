package com.line.line_demo.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.line.line_demo.dto.Message;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendBody {

    String to;

    List<Message> messages;

}
