package com.line.line_demo.config.kafka;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topic.send-notification.name}")
    String sendNotification;

    @Value("${spring.kafka.topic.send-message.name}")
    String sendMessage;


    @Profile("local")
    @Bean
    public NewTopic createSendNotificationTopicLocal() {
        return new NewTopic(sendNotification, 3, (short) 2);
    }

    @Profile("local")
    @Bean
    public NewTopic createSendMessageTopicLocal() {
        return new NewTopic(sendMessage, 3, (short) 2);
    }

}
