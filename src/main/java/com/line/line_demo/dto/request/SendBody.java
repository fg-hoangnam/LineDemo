package com.line.line_demo.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.line.line_demo.dto.Message;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendBody {

    @Schema(description = "User or Group ID to send to", example = "0967456235")
    @NotBlank(message = "Submissions must not be left blank")
    String to;

    @Schema(description = "List of messages to send", example = "[{\"type\": \"text\", \"text\": \"Hi there!\"}]")
    @NotEmpty(message = "The content of the notification must not be left blank")
    List<Message> messages;

}
