package com.line.line_demo.config.kafka;

import com.line.line_demo.utils.JsonMapperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageInterceptor {

    /** Thông báo nhật ký để bắt đầu đẩy tin nhắn vào queue. */
    private static final String LOG_START =
            "Start push message to queue: {} messageId: {} with payload: {}";

    /** Thông báo nhật ký kết thúc đẩy tin nhắn vào queue. */
    private static final String LOG_END = "End push message to queue: {} messageId: {}";

    /** Mẫu Kafka để gửi tin nhắn đến queue Kafka. */
    @SuppressWarnings("rawtypes")
    private final KafkaTemplate kafkaTemplate;

    /**
     * Phương thức này được sử dụng để chuyển đổi dữ liệu tin nhắn sang JSON và gửi nó đến một hàng đợi Kafka được chỉ định.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void convertAndSend(String queueName, MessageData payload) {
        var payloadJson = JsonMapperUtils.toJson(payload);
        log.info(LOG_START, queueName, payload.getMessageId(), payloadJson);
        kafkaTemplate.send(queueName, payloadJson);
        log.info(LOG_END, queueName, payload.getMessageId());
    }

    /**
     * Phương thức này được sử dụng để chuyển đổi dữ liệu tin nhắn sang JSON và gửi nó đến một hàng đợi Kafka được chỉ định
     * với một khóa được chỉ định.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void convertAndSend(String queueName, String key, MessageData payload) {
        var payloadJson = JsonMapperUtils.toJson(payload);
        log.info(LOG_START, queueName, payload.getMessageId(), payloadJson);
        kafkaTemplate.send(queueName, key, payloadJson);
        log.info(LOG_END, queueName, payload.getMessageId());
    }

}