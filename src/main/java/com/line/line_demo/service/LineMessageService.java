package com.line.line_demo.service;

import com.line.line_demo.dto.Message;
import com.line.line_demo.dto.request.SendBody;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface LineMessageService {

    /**
     * Gửi một tin nhắn văn bản đơn giản.
     *
     * @param message nội dung tin nhắn
     * @return kết quả gửi
     */
    Object sendMessage(String accessToken,String message);

    /**
     * Gửi một tin nhắn với định dạng phức tạp hơn (sử dụng SendBody).
     *
     * @param data dữ liệu tin nhắn cần gửi
     * @return CompletableFuture để xử lý async kết quả gửi
     */
    CompletableFuture<Object> sendMessage(SendBody data);

    /**
     * Gửi một danh sách tin nhắn (batch).
     *
     * @param input danh sách các SendBody cần gửi
     */
    void handleSendMessage(List<SendBody> input);

    /**
     * Gửi nhiều message cùng lúc theo định dạng `Message`.
     *
     * @param request danh sách message
     * @return kết quả gửi
     */
    Object sendMultipleMessage(List<Message> request);

    /**
     * Lấy thông tin giới hạn gửi tin nhắn hiện tại của hệ thống.
     *
     * @return dữ liệu về hạn mức gửi (quota, remaining, etc.)
     */
    Object getSendLimit();

    /**
     * Truy vấn số lượng tin nhắn đã gửi trong thời gian hiện tại (ngày, tháng, etc.).
     *
     * @return số lượng message đã gửi
     */
    Object getNumSentMessage();

    Object sendMultipleMessageNonKafka(List<Message> request);


}
