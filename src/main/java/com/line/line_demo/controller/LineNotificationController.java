package com.line.line_demo.controller;

import com.line.line_demo.dto.Message;
import com.line.line_demo.dto.WebhookEvent;
import com.line.line_demo.dto.request.SendBody;
import com.line.line_demo.service.LineNotificationService;
import com.line.line_demo.service.LineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
@Slf4j
@RequiredArgsConstructor
public class LineNotificationController {

    private final LineNotificationService lineService;

    @PostMapping("/send-multiple-notification")
    public ResponseEntity<?> sendMultipleNotification(
            @RequestBody @Valid List<SendBody> request
    ){
        return ResponseEntity.ok(lineService.sendMultipleNotification(request));
    }

    @GetMapping("/send-noti")
    public ResponseEntity<?> sendNotification(
           @RequestBody @Valid SendBody request
    ){
        return ResponseEntity.ok(lineService.sendNotification(request));
    }

}
