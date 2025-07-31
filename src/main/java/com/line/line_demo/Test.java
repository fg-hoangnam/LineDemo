package com.line.line_demo;

import com.line.line_demo.utils.JsonMapperUtils;
import com.linecorp.bot.webhook.model.CallbackRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
public class Test {

    public static String body = """
            {
              "destination": "Uc7472b39e21dab71c2347e02714630d6",
              "events": [
                {
                  "type": "delivery",
                  "delivery": {
                    "data": "15034552939884E28681A7D668CEA94C147C716C0EC9DFE8B80B44EF3B57F6BD0602366BC3menu01"
                  },
                  "webhookEventId": "01G17EJCGAVV66J5WNA7ZCTF6H",
                  "deliveryContext": {
                    "isRedelivery": false
                  },
                  "timestamp": 1650591346705,
                  "mode": "active"
                },
                 {
                            "replyToken": "nHuyWiB7yP5Zw52FIkcQobQuGDXCTA",
                            "type": "message",
                            "mode": "active",
                            "timestamp": 1462629479859,
                            "source": {
                                "type": "group",
                                "groupId": "Ca56f94637c...",
                                "userId": "U4af4980629..."
                            },
                            "webhookEventId": "01FZ74A0TDDPYRVKNK77XKC3ZR",
                            "deliveryContext": {
                                "isRedelivery": false
                            },
                            "message": {
                                "id": "444573844083572737",
                                "type": "text",
                                "quoteToken": "q3Plxr4AgKd...",
                                "text": "@All @example Good Morning!! (love)",
                                "emojis": [
                                    {
                                        "index": 29,
                                        "length": 6,
                                        "productId": "5ac1bfd5040ab15980c9b435",
                                        "emojiId": "001"
                                    }
                                ],
                                "mention": {
                                    "mentionees": [
                                        {
                                            "index": 0,
                                            "length": 4,
                                            "type": "all"
                                        },
                                        {
                                            "index": 5,
                                            "length": 8,
                                            "userId": "U49585cd0d5...",
                                            "type": "user",
                                            "isSelf": false
                                        }
                                    ]
                                }
                            }
                        }
              ]
            }
            """;

    public static void main(String[] args) {

        CallbackRequest request = JsonMapperUtils.convertJsonToObject(body, CallbackRequest.class);

        List<List<String>> res = splitIntoBatches(List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"), 3);
        System.out.println(res);
    }

    public static List<List<String>> splitIntoBatches(List<String> messages, int batchSize) {
        List<List<String>> batches = new ArrayList<>();
        for (int i = 0; i < messages.size(); i += batchSize) {
            batches.add(messages.subList(i, Math.min(i + batchSize, messages.size())));
        }
        return batches;
    }

}
