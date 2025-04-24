package com.line.line_demo.controller;


import com.line.line_demo.dto.WebhookEvent;
import com.line.line_demo.service.LineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@Slf4j
@RequiredArgsConstructor
public class LineController {

    private final LineService lineService;

    @GetMapping("/callback")
    public ResponseEntity<Object> handleCallback(@RequestParam String payload) {
        return ResponseEntity.ok(lineService.handleCallback(payload));
    }

    @PostMapping("/webhook")
    public ResponseEntity<WebhookEvent> handleWebhook(@RequestBody String payload) {
        return ResponseEntity.ok(lineService.handleWebhook(payload));
    }

}
