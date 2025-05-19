package com.line.line_demo.service.impl;

import com.line.line_demo.config.kafka.KafkaTopicConfig;
import com.line.line_demo.entities.LineAccountInfo;
import com.line.line_demo.repository.LineAccountInfoRepository;
import com.line.line_demo.service.LineService;
import com.line.line_demo.utils.JsonMapperUtils;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.message.FlexMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.flex.container.FlexContainer;
import com.linecorp.bot.webhook.model.MessageEvent;
import com.linecorp.bot.webhook.model.PostbackEvent;
import com.linecorp.bot.webhook.model.TextMessageContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
@Service
@Slf4j
@RequiredArgsConstructor
public class LineServiceImpl implements LineService {

    private final LineAccountInfoRepository LineAccountInfoRepository;

    private final LineMessagingClient lineMessagingClient;

    @Value("${line.api.rate-limit}")
    private int RATE_LIMIT;

    @SuppressWarnings("rawtypes")
    private final KafkaTemplate kafkaTemplate;

    private final KafkaTopicConfig kafkaTopicConfig;

    @Override
    public void handleMessageEvent(MessageEvent event) {
        log.info("[Received webhook] data : {}", event);
        log.info("[Received webhook] User Profile : {}", lineMessagingClient.getProfile(event.source().userId()));
        saveLineAccountInfoId(event.source().userId());
        if(event.message() instanceof TextMessageContent){
            if(((TextMessageContent) event.message()).text().equals("flex")){

            }
        }

        sendFlexMessage(null, event.replyToken());
    }

    @Override
    public void handlePostbackEvent(PostbackEvent event) {
        //
    }

    private void sendFlexMessage(String userId, String replyToken){

        String flexJson = """
                  {
                  "type": "bubble",
                  "hero": {
                    "type": "image",
                    "url": "https://developers-resource.landpress.line.me/fx/img/01_2_restaurant.png",
                    "size": "full",
                    "aspectRatio": "20:13",
                    "aspectMode": "cover",
                    "action": {
                      "type": "uri",
                      "uri": "https://line.me/"
                    }
                  },
                  "body": {
                    "type": "box",
                    "layout": "vertical",
                    "spacing": "md",
                    "action": {
                      "type": "uri",
                      "uri": "https://line.me/"
                    },
                    "contents": [
                      {
                        "type": "text",
                        "text": "Brown's Burger",
                        "size": "xl",
                        "weight": "bold"
                      },
                      {
                        "type": "box",
                        "layout": "vertical",
                        "spacing": "sm",
                        "contents": [
                          {
                            "type": "box",
                            "layout": "baseline",
                            "contents": [
                              {
                                "type": "icon",
                                "url": "https://developers-resource.landpress.line.me/fx/img/restaurant_regular_32.png"
                              },
                              {
                                "type": "text",
                                "text": "$10.5",
                                "weight": "bold",
                                "margin": "sm",
                                "flex": 0
                              },
                              {
                                "type": "text",
                                "text": "400kcl",
                                "size": "sm",
                                "align": "end",
                                "color": "#aaaaaa"
                              }
                            ]
                          },
                          {
                            "type": "box",
                            "layout": "baseline",
                            "contents": [
                              {
                                "type": "icon",
                                "url": "https://developers-resource.landpress.line.me/fx/img/restaurant_large_32.png"
                              },
                              {
                                "type": "text",
                                "text": "$15.5",
                                "weight": "bold",
                                "margin": "sm",
                                "flex": 0
                              },
                              {
                                "type": "text",
                                "text": "550kcl",
                                "size": "sm",
                                "align": "end",
                                "color": "#aaaaaa"
                              }
                            ]
                          }
                        ]
                      },
                      {
                        "type": "text",
                        "text": "Sauce, Onions, Pickles, Lettuce & Cheese",
                        "wrap": true,
                        "color": "#aaaaaa",
                        "size": "xxs"
                      }
                    ]
                  },
                  "footer": {
                    "type": "box",
                    "layout": "vertical",
                    "contents": [
                      {
                        "type": "button",
                        "style": "primary",
                        "color": "#905c44",
                        "margin": "xxl",
                        "action": {
                          "type": "uri",
                          "label": "Add to Cart",
                          "uri": "https://line.me/"
                        }
                      }
                    ]
                  }
                }
                """;

        FlexContainer flexContainer = JsonMapperUtils.convertJsonToObject(flexJson, FlexContainer.class);

        lineMessagingClient.replyMessage(
                new ReplyMessage(
                        replyToken,
                        FlexMessage.builder().altText("alt text").contents(flexContainer).build()
                )
        );
    }

    private void sendTextMessage(String userId){
        lineMessagingClient.pushMessage(
            new PushMessage(
                userId,
                TextMessage.builder()
                    .text("Hello push").build()
            )
        );
    }

    private void createRichMenu(String userId){

    }

    private void saveLineAccountInfoId(String userId) {
        if (!LineAccountInfoRepository.existsByLineId(userId)) {
            LineAccountInfo newUserList = LineAccountInfoRepository.save(new LineAccountInfo(userId));
            log.info("[Save line userId] success: {}", newUserList);
        }
    }

}
