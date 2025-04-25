package com.line.line_demo.service;

import com.line.line_demo.dto.WebhookEvent;

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

}

