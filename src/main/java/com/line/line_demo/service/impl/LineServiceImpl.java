package com.line.line_demo.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.line.line_demo.config.kafka.KafkaTopicConfig;
import com.line.line_demo.config.kafka.MessageData;
import com.line.line_demo.dto.Event;
import com.line.line_demo.dto.Message;
import com.line.line_demo.dto.Source;
import com.line.line_demo.dto.WebhookEvent;
import com.line.line_demo.dto.request.SendBody;
import com.line.line_demo.entities.LineUser;
import com.line.line_demo.event.base.DataLine;
import com.line.line_demo.repository.LineUserRepository;
import com.line.line_demo.service.LineService;
import com.line.line_demo.utils.APIUtils;
import com.line.line_demo.utils.JsonMapperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class LineServiceImpl implements LineService {

    private final LineUserRepository lineUserRepository;

    @Value("${line.access-token}")
    private String ACCESS_TOKEN;

    @Value("${line.api.rate-limit}")
    private int RATE_LIMIT;

    @Value("${line.api.base-url}")
    private String BASE_URL;

    @Value("${line.api.message.push}")
    private String SEND_MESSAGE;

    @Value("${line.api.noti.push}")
    private String SEND_NOTI;

    @Value("${line.api.message.get-limit-per-month}")
    private String LIMIT_PER_MONTH;

    @Value("${line.api.message.get-number-sent-message}")
    private String NUM_SENT_MESSAGE;

    @SuppressWarnings("rawtypes")
    private final KafkaTemplate kafkaTemplate;

    private final KafkaTopicConfig kafkaTopicConfig;

    @Override
    public WebhookEvent handleWebhook(String payload) {
        log.info("[Received webhook] data : {}", payload);
        WebhookEvent data = JsonMapperUtils.convertJsonToObject(payload, WebhookEvent.class);
        if (ObjectUtils.isEmpty(data)) return null;
        saveLineUserId(data);
        log.info("[Webhook processed] data : {}", payload);
        return data;
    }

    @Override
    public Object handleCallback(String payload) {
        log.info("[Line Callback] callback : {}", payload);
        return payload;
    }

    private void saveLineUserId(WebhookEvent data) {
        if (!CollectionUtils.isEmpty(data.getEvents())) {
            List<String> userIds = data.getEvents().stream().map(Event::getSource).map(Source::getUserId).toList();
            if (!CollectionUtils.isEmpty(userIds)) {
                List<String> userExisted = lineUserRepository.findAllByUserIdIn(userIds);
                if (CollectionUtils.isEmpty(userExisted) || userExisted.size() != userIds.size()) {
                    List<LineUser> newUserList = lineUserRepository.saveAll(userIds.stream()
                            .filter(u -> !userExisted.contains(u))
                            .map(LineUser::new)
                            .toList());
                    log.info("[Save line userId] success: {}", newUserList);
                }
            }
        }
    }

}
