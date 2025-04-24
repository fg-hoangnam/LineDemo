package com.line.line_demo.service;

import com.line.line_demo.dto.Message;
import com.line.line_demo.dto.WebhookEvent;
import com.line.line_demo.dto.request.SendBody;

import java.util.List;
import java.util.concurrent.CompletableFuture;
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
    WebhookEvent handleWebhook(String payload);

    /**
     * Xử lý callback payload từ LINE (thường dùng cho postback hoặc interactive message).
     *
     * @param payload chuỗi JSON raw callback
     * @return kết quả tùy theo logic xử lý
     */
    Object handleCallback(String payload);

}

