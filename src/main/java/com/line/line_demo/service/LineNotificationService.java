package com.line.line_demo.service;

import com.line.line_demo.dto.request.SendBody;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface LineNotificationService {

    /**
     * Gửi notification async với cấu trúc `SendBody`.
     *
     * @param data thông tin notification
     * @return CompletableFuture kết quả gửi
     */
    CompletableFuture<Object> sendNotification(SendBody data);

    /**
     * Gửi nhiều notification một lúc từ danh sách input.
     *
     * @param input danh sách notification
     */
    void handleSendNotification(List<SendBody> input);

    /**
     * Gửi nhiều notification theo định dạng `SendBody`.
     *
     * @param request danh sách message notification
     * @return kết quả gửi
     */
    Object sendMultipleNotification(List<SendBody> request);

}
