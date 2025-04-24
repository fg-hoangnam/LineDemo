package com.line.line_demo.event.kafka;

import com.line.line_demo.config.kafka.MessageData;
import com.line.line_demo.config.kafka.MessageListener;
import com.line.line_demo.event.base.DataLine;
import com.line.line_demo.service.LineMessageService;
import com.line.line_demo.service.LineNotificationService;
import com.line.line_demo.service.LineService;
import com.line.line_demo.utils.JsonMapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SendNotificationConsumer extends MessageListener<DataLine> {

    @Value("${spring.kafka.topic.send-notification.name}")
    String sendNotification;

    @Value("${spring.kafka.topic.send-message.name}")
    String sendMessage;

    @Autowired
    private LineMessageService lineMessageService;

    @Autowired
    private LineNotificationService lineNotificationService;

    @KafkaListener(
            topics = "${spring.kafka.topic.send-message.name}",
            groupId = "${spring.kafka.messaging.kafka.groupId}",
            concurrency = "${spring.kafka.topic.send-message.concurrent.thread}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void setSendMessageEventListener(
            String data,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) String partition,
            @Header(KafkaHeaders.OFFSET) String offset,
            Acknowledgment acknowledgment) {
        super.messageListener(data, topic, partition, offset, acknowledgment, 0, 0, sendMessage);
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.send-notification.name}",
            groupId = "${spring.kafka.messaging.kafka.groupId}",
            concurrency = "${spring.kafka.topic.send-notification.concurrent.thread}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void setSendNotificationEventListener(
            String data,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) String partition,
            @Header(KafkaHeaders.OFFSET) String offset,
            Acknowledgment acknowledgment) {
        super.messageListener(data, topic, partition, offset, acknowledgment, 0, 0, sendNotification);
    }

    @Override
    protected void handleMessageEvent(String topic, String partition, String offset, MessageData<DataLine> input) {
        log.info("Handle send notification: {}", JsonMapperUtils.writeValueAsString(input.getContent()));
        try {
            if(topic.equals(sendNotification)){
                //todo: handle send notification
                lineNotificationService.handleSendNotification(input.getContent().getData());

            } else if (topic.equals(sendMessage)) {
                //todo: handle send message
                lineMessageService.handleSendMessage(input.getContent().getData());
            }

        } catch (Exception e) {
            log.warn("Error when handle send notification: {}", e.getMessage());
            //todo: handle the case the message is not expected
        }

    }
}
