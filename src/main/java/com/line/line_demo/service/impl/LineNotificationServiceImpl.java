package com.line.line_demo.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.line.line_demo.config.exception.ServiceException;
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
import com.line.line_demo.service.LineNotificationService;
import com.line.line_demo.service.LineService;
import com.line.line_demo.utils.APIUtils;
import com.line.line_demo.utils.JsonMapperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.rmi.ServerException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class LineNotificationServiceImpl implements LineNotificationService {

    private final LineUserRepository lineUserRepository;

    @Value("${line.api.rate-limit}")
    private int RATE_LIMIT;

    @Value("${line.access-token}")
    private String ACCESS_TOKEN;

    @Value("${line.api.base-url}")
    private String BASE_URL;

    @Value("${line.api.noti.push}")
    private String SEND_NOTI;

    @SuppressWarnings("rawtypes")
    private final KafkaTemplate kafkaTemplate;

    private final KafkaTopicConfig kafkaTopicConfig;

    @Override
    public Object sendNotification(String phone, String message) {
        log.info("[Send Noti] text: {}", message);
        try {
            APIUtils apiUtils = new APIUtils();
            if(phone != null && message != null){
                Object res = apiUtils.callApiSendJson(
                        (BASE_URL + SEND_NOTI),
                        HttpMethod.POST,
                        null,
                        APIUtils.getAdditionalHeader(ACCESS_TOKEN, Map.of("X-Line-Delivery-Tag", "test-noti")),
                        null,
                        new SendBody(
                                hashPhoneNumber(phone),
                                Collections.singletonList(new Message("text", message))
                        ),
                        new TypeReference<>() {
                        }
                );
                return res;
            }
            throw ServiceException.badRequest("Request to enter Phone Number and Messages!");
        } catch (Exception e) {
            throw ServiceException.badRequest(e.getMessage());
        }
    }

    /**
     * Tương tự `sendMessage` nhưng dùng cho loại thông báo (notification).
     * Gửi theo thread riêng với `@Async`.
     */
    @Override
    @Async("taskExecutor")
    public CompletableFuture<Object> sendNotification(SendBody data) {
        //note: yêu cầu hashing phone number
        try {
            APIUtils apiUtils = new APIUtils();
            return CompletableFuture.completedFuture(apiUtils.callApiSendJson(
                            (BASE_URL + SEND_NOTI),
                            HttpMethod.POST,
                            null,
                            APIUtils.getAdditionalHeader(ACCESS_TOKEN, Map.of("X-Line-Delivery-Tag", "test-noti")),
                            null,
                            data,
                            new TypeReference<>() {
                            }
                    )).whenComplete(
                            (rs, ex) -> log.info("[Send Message] Successfully. Thread: {} | Time: {} | Result: {}", Thread.currentThread().getName(), LocalDateTime.now(), JsonMapperUtils.writeValueAsString(rs)))
                    .exceptionally(ex -> {
                                log.info("[Send Message] Failed. Thread: {} | Time: {} | Error: {}", Thread.currentThread().getName(), LocalDateTime.now(), ex.getMessage());
                                return CompletableFuture.completedFuture(null);
                            }
                    );
        } catch (Exception e) {
            log.error("[Send Noti] Failed to send noti to: {}", JsonMapperUtils.writeValueAsString(data), e);
            return CompletableFuture.completedFuture(null);
        }
    }

    /**
     * Gửi danh sách thông báo bất đồng bộ bằng cách gọi `sendNotification`.
     */
    @Override
    public void handleSendNotification(List<SendBody> input) {
        log.info("[Send Noti] Start handle send notification, Time: {}", LocalDateTime.now());
        //note: yêu cầu hashing phone number
        input.forEach(this::sendNotification);
        log.info("[Send Noti] Start handle send notification, Time: {}", LocalDateTime.now());
    }

    @Override
    public Object sendMultipleNotification(List<Message> request) {
        //todo: send kafka template
        return null;
    }

    public static String hashPhoneNumber(String phoneNumber) {
        // Loại bỏ dấu '-' nếu có, đảm bảo chuẩn E.164
        String normalized = phoneNumber.replace("-", "").trim();
        return DigestUtils.sha256Hex(normalized);
    }

}
