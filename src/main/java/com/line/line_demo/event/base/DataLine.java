package com.line.line_demo.event.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.line.line_demo.dto.request.SendBody;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataLine {

    List<SendBody> data;

}
