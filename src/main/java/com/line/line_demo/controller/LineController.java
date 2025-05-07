package com.line.line_demo.controller;


import com.line.line_demo.service.LineService;

import com.linecorp.bot.spring.boot.handler.annotation.EventMapping;
import com.linecorp.bot.spring.boot.handler.annotation.LineMessageHandler;
import com.linecorp.bot.webhook.model.MessageEvent;
import com.linecorp.bot.webhook.model.PostbackEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@Slf4j
@RequiredArgsConstructor
@LineMessageHandler
public class LineController {

    private final LineService lineService;

    @EventMapping // Xử lý sự kiện tin nhắn dạng Text
    public void handleTextMessageEvent(MessageEvent event) {
        log.info("Received message event: {}", event);
        lineService.handleMessageEvent(event);
    }

    @EventMapping // Xử lý sự kiện tin nhắn dạng Text
    public void handlePostbackEventEvent(PostbackEvent event) {
        log.info("Received message event: {}", event);
        lineService.handlePostbackEvent(event);
    }

}
