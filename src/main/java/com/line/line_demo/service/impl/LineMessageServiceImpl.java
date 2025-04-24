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
import com.line.line_demo.service.LineMessageService;
import com.line.line_demo.service.LineService;
import com.line.line_demo.utils.APIUtils;
import com.line.line_demo.utils.JsonMapperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
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
public class LineMessageServiceImpl implements LineMessageService {

    private final LineUserRepository lineUserRepository;

    @Value("${line.api.rate-limit}")
    private int RATE_LIMIT;

    @Value("${line.api.base-url}")
    private String BASE_URL;

    @Value("${line.api.message.push}")
    private String SEND_MESSAGE;

    @Value("${line.api.message.get-limit-per-month}")
    private String LIMIT_PER_MONTH;

    @Value("${line.api.message.get-number-sent-message}")
    private String NUM_SENT_MESSAGE;

    @SuppressWarnings("rawtypes")
    private final KafkaTemplate kafkaTemplate;

    private final KafkaTopicConfig kafkaTopicConfig;


    /**
     * Gửi tin nhắn bất đồng bộ qua LINE Messaging API.
     * Sử dụng @Async để chạy trong thread pool riêng, giúp không block thread chính.
     * Dữ liệu gửi kèm dạng `SendBody`. Kết quả được wrap trong CompletableFuture.
     */
    @Override
    @Async("taskExecutor")
    public CompletableFuture<Object> sendMessage(SendBody data) {
        try {
            APIUtils apiUtils = new APIUtils();
            return CompletableFuture.completedFuture(apiUtils.callApiSendJson(
                            (BASE_URL + SEND_MESSAGE),
                            HttpMethod.POST,
                            null,
                            APIUtils.getAdditionalHeader(null),
                            null,
                            data,
                            new TypeReference<>() {
                            }
                    )).whenComplete(
                            (rs, ex) -> log.info("[Send Message] Successfully. Thread: {} | Time: {} | Result: {}", Thread.currentThread().getName(), LocalDateTime.now(), JsonMapperUtils.writeValueAsString(rs)))
                    .exceptionally(ex -> {
                        log.info("[Send Message] Failed. Thread: {} | Time: {} | Error: {}", Thread.currentThread().getName(), LocalDateTime.now(), ex.getMessage());
                        return CompletableFuture.completedFuture(null);
                    });
        } catch (Exception e) {
            log.error("[Send Message] Failed to send message to: {}", JsonMapperUtils.writeValueAsString(data), e);
            return CompletableFuture.completedFuture(null);
        }
    }

    /**
     * Xử lý gửi nhiều tin nhắn bằng cách gọi `sendMessage` cho từng phần tử.
     */
    @Override
    public void handleSendMessage(List<SendBody> input) {
        log.info("[Send message] Start handle send message, Time: {}", LocalDateTime.now());
        input.forEach(this::sendMessage);
        log.info("[Send message] Successfully send message, Time: {}", LocalDateTime.now());
    }

    /**
     * Tạo danh sách tin nhắn và gửi lên Kafka topic để xử lý song song.
     * Có xử lý chia nhỏ mảng nếu vượt quá ngưỡng `RATE_LIMIT`.
     */
    @Override
    public Object sendMultipleMessage(List<Message> request) {
        validateLimitSendMessage();
        // user executor thread to handle list
        LineUser lineUser = lineUserRepository.findAll().getFirst();
        List<SendBody> data = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            data.add(new SendBody(
                    lineUser.getLindId(),
                    Collections.singletonList(new Message("text", "hi"))
            ));
        }

        List<List<SendBody>> result = new ArrayList<>();
        int totalSize = data.size();
        int limit = RATE_LIMIT;
        if (data.size() > limit) {
            for (int i = 0; i < totalSize; i += (i + limit > totalSize ? totalSize - i : limit)) {
                result.add(new ArrayList<>(data.subList(i, i + (i + limit > totalSize ? totalSize - i : limit))));
            }
        }else{
            result.add(data);
        }

        result.forEach(sendBodies -> {
            kafkaTemplate.send(
                    kafkaTopicConfig.getSendMessage(),
                    JsonMapperUtils.toJson(new MessageData<>(new DataLine(sendBodies)))
            );
        });
        return null;
    }

    @Override
    public Object sendMessage(String accessToken, String message) {
        log.info("[Send Message] text: {}", message);
        validateLimitSendMessage();
        try {
            APIUtils apiUtils = new APIUtils();
            LineUser lineUser = lineUserRepository.findAll().getFirst();
            return apiUtils.callApiSendJson(
                    (BASE_URL + SEND_MESSAGE),
                    HttpMethod.POST,
                    null,
                    APIUtils.getAdditionalHeader(Map.of("X-Line-Delivery-Tag", "test-message")),
                    null,
                    new SendBody(
                            lineUser.getLindId(),
                            Collections.singletonList(new Message("text", message))
                    ),
                    new TypeReference<>() {
                    }
            );
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Gửi tin nhắn hàng loạt không thông qua Kafka (chạy trực tiếp).
     * Có đo thời gian xử lý để đánh giá hiệu suất.
     */
    @Override
    public Object sendMultipleMessageNonKafka(List<Message> request) {
        Long start = System.currentTimeMillis();
        validateLimitSendMessage();
        LineUser lineUser = lineUserRepository.findAll().getFirst();
        List<SendBody> data = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            data.add(new SendBody(
                    lineUser.getLindId(),
                    Collections.singletonList(new Message("text", "hi"))
            ));
        }

        List<List<SendBody>> result = new ArrayList<>();
        int totalSize = data.size();
        int limit = RATE_LIMIT;
        if (data.size() > limit) {
            for (int i = 0; i < totalSize; i += (i + limit > totalSize ? totalSize - i : limit)) {
                result.add(new ArrayList<>(data.subList(i, i + (i + limit > totalSize ? totalSize - i : limit))));
            }
        }else{
            result.add(data);
        }
        result.forEach(sendBodies -> sendBodies.forEach(this::sendMessage));
        return System.currentTimeMillis() - start;
    }


    @Override
    public Message getSendLimit() {
        log.info("[Get send limit per month]");
        try {
            APIUtils apiUtils = new APIUtils();
            return apiUtils.callApiSendJson(
                    (BASE_URL + LIMIT_PER_MONTH),
                    HttpMethod.GET,
                    null,
                    APIUtils.getAdditionalHeader(null),
                    null,
                    null,
                    new TypeReference<>() {
                    }
            );
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Message getNumSentMessage() {
        log.info("[Get Num Sent Message]");
        try {
            APIUtils apiUtils = new APIUtils();
            return apiUtils.callApiSendJson(
                    (BASE_URL + NUM_SENT_MESSAGE),
                    HttpMethod.GET,
                    null,
                    APIUtils.getAdditionalHeader(null),
                    null,
                    null,
                    new TypeReference<>() {
                    }
            );
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validateLimitSendMessage() {

        Message limit = getSendLimit();
        Message numSentMessage = getNumSentMessage();

        if(
                limit != null && numSentMessage != null &&
                limit.getSendLimit() != null && numSentMessage.getTotalUsage() != null &&
                numSentMessage.getTotalUsage() >= limit.getSendLimit()
        ){
            log.info("[Send Message] Failed. Total Usage: {}", numSentMessage.getTotalUsage());
            throw new RuntimeException("The number of times sending the message exceeds the limit");
        }
    }

}
