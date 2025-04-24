package com.line.line_demo.config.kafka;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Generic wrapper class dùng để đóng gói dữ liệu gửi/nhận qua Kafka.
 * Bao gồm thông tin messageId duy nhất, subject định danh logic, và nội dung generic content.
 *
 * @param <T> Kiểu dữ liệu thực tế của nội dung message.
 */
@Getter
@NoArgsConstructor
public class MessageData<T> {

    /**
     * UUID đại diện cho ID duy nhất của message.
     * Được tự động sinh trong constructor trừ khi override bằng updateMessageId().
     */
    private String messageId;

    /**
     * Subject đại diện cho "chủ đề" logic, có thể dùng để định tuyến hoặc lọc message.
     */
    private String subject;

    /**
     * Nội dung thực tế của message, kiểu dữ liệu generic cho phép tái sử dụng linh hoạt.
     */
    private T content;

    /**
     * Constructor khởi tạo MessageData với content,
     * đồng thời sinh tự động messageId.
     *
     * @param content nội dung thực tế của message.
     */
    public MessageData(T content) {
        this.messageId = UUID.randomUUID().toString(); // Tạo UUID ngẫu nhiên cho message
        this.content = content;
    }

    /**
     * Constructor khởi tạo MessageData với subject và content.
     * Tự động sinh messageId.
     *
     * @param subject tên định danh logic cho message.
     * @param content nội dung thực tế của message.
     */
    public MessageData(String subject, T content) {
        this(content);
        this.subject = subject;
    }

    /**
     * Cho phép cập nhật lại messageId (nếu cần override mặc định).
     * Lưu ý: Việc sửa messageId có thể ảnh hưởng đến khả năng tracking.
     *
     * @param messageId ID mới cho message.
     */
    public void updateMessageId(String messageId) {
        this.messageId = messageId;
    }
}
