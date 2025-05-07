package com.line.line_demo.config;

import com.linecorp.bot.client.LineMessagingClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class LineBotConfig {

    @Value("${line.bot.channel-token}")
    private String CHANNEL_ACCESS_TOKEN;

    @Bean
    public LineMessagingClient lineMessagingClient() {
        return LineMessagingClient.builder(CHANNEL_ACCESS_TOKEN).build();
    }
}