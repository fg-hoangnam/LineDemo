package com.line.line_demo.controller;

import com.linecorp.bot.webhook.model.CallbackRequest;
import com.linecorp.bot.webhook.model.PnpDeliveryCompletionEvent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpHeaders;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@Slf4j
@RequiredArgsConstructor
public class LineTestController {


    @PostMapping("/callback") // Xử lý sự kiện tin nhắn dạng Text 4
    public void handleTextMessageEvent(
            HttpServletRequest request,
            @RequestBody CallbackRequest event
    ) {
        log.info("Webhook Received at 1: {} | {}", LocalDateTime.now(), event);
        List<PnpDeliveryCompletionEvent> pnpEvent = event.events().stream()
                .filter(e -> e instanceof PnpDeliveryCompletionEvent)
                .map(e -> (PnpDeliveryCompletionEvent) e)
                .toList();
//        lineService.handleMessageEvent(event);
    }

//    @EventMapping // Xử lý sự kiện tin nhắn dạng Text
//    public void handlePostbackEventEvent(PostbackEvent event) {
//        log.info("Received message event: {}", event);
//        lineService.handlePostbackEvent(event);
//    }

}
