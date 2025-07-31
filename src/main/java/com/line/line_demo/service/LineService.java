package com.line.line_demo.service;

//import com.line.line_demo.dto.WebhookEvent;
//import com.linecorp.bot.model.message.TextMessage;
//import com.linecorp.bot.webhook.model.CallbackRequest;
import com.linecorp.bot.webhook.model.MessageEvent;
import com.linecorp.bot.webhook.model.PostbackEvent;

/**
 * LineService định nghĩa các hành vi tương tác với LINE Messaging API,
 * bao gồm xử lý webhook, gửi message/notification đơn lẻ hoặc hàng loạt,
 * và các truy vấn liên quan đến giới hạn gửi.
 */
public interface LineService {

    /**
     * Xử lý payload webhook từ LINE (sự kiện như tin nhắn, follow, unfollow, v.v.).
     *
     * @param payload chuỗi JSON raw từ LINE webhook
     * @return đối tượng WebhookEvent đã parse
     */
    void handleMessageEvent(MessageEvent event);

    void handlePostbackEvent(PostbackEvent event);

}

