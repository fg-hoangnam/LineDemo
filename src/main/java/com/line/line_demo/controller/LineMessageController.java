package com.line.line_demo.controller;

import com.line.line_demo.dto.Message;
import com.line.line_demo.dto.WebhookEvent;
import com.line.line_demo.service.LineMessageService;
import com.line.line_demo.service.LineService;
import com.line.line_demo.utils.JsonMapperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/message")
@Slf4j
@RequiredArgsConstructor
public class LineMessageController {

    private final LineMessageService lineService;
    // 1
    @PostMapping("/send-message")
    public ResponseEntity<?> sendMessage(String accessToken, String message){
        return ResponseEntity.ok(lineService.sendMessage(accessToken, message));
    }

    @PostMapping("/send-multiple-message")
    public ResponseEntity<?> sendMultipleMessage(
            @RequestBody List<Message> request
    ){
        return ResponseEntity.ok(lineService.sendMultipleMessage(request));
    }

    @PostMapping("/send-multiple-message-non-kafka")
    public ResponseEntity<?> sendMultipleMessageNonKafka(
            @RequestBody List<Message> request
    ){
        return ResponseEntity.ok(lineService.sendMultipleMessageNonKafka(request));
    }

    @GetMapping("/get-send-limit")
    public ResponseEntity<?> getSendLimit(){
        return ResponseEntity.ok(lineService.getSendLimit());
    }

    @GetMapping("/get-num-sent-message")
    public ResponseEntity<?> getNumSentMessage(){
        return ResponseEntity.ok(lineService.getNumSentMessage());
    }

}
