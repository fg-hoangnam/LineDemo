package com.line.line_demo.config.kafka;

import com.line.line_demo.utils.JsonMapperUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.Acknowledgment;

import java.lang.reflect.ParameterizedType;
import java.util.UUID;

/**
 * Abstract class dùng làm base cho các Kafka consumer listener cụ thể.
 * Sử dụng generic type <T> để cho phép parse và xử lý message linh hoạt với nhiều loại dữ liệu.
 *
 * @param <T> Kiểu dữ liệu thực tế của nội dung message.
 */
@Slf4j
public abstract class MessageListener<T> {

    /**
     * Interceptor cho phép chặn, log hoặc can thiệp vào quá trình xử lý message.
     * Có thể null nếu không cần xử lý trung gian.
     */
    protected MessageInterceptor messageInterceptor;

    /**
     * Phương thức này được gọi sau khi Spring khởi tạo bean.
     * Mục đích là để kiểm tra tính hợp lệ của generic type T (tránh lỗi khi deserialize).
     */
    @PostConstruct
    public void checkGenericTypeSupport() {
        try {
            this.getMessageContentType();
        } catch (Exception e) {
            log.error("MessageListener generic type support failed.");
            throw e;
        }
    }

    /**
     * Hàm lắng nghe message Kafka, với các tham số cơ bản.
     * Có thể override thêm delay, retry, và DLQ topic nếu cần.
     */
    @SuppressWarnings("unused")
    public void messageListener(
            String data, String topic, String partition, String offset, Acknowledgment acknowledgment) {
        messageListener(data, topic, partition, offset, acknowledgment, 0, 0, null);
    }

    /**
     * Hàm chính xử lý message, có hỗ trợ thêm delay, retry count, và tên DLQ topic.
     */
    public void messageListener(
            String data,
            String topic,
            String partition,
            String offset,
            Acknowledgment acknowledgment,
            long delayTime,
            Integer repeatCount,
            String dlqTopicName
    ) {
        this.initListener(topic, partition, offset, data);

        // Deserialize JSON thành MessageData<T> với type runtime
        MessageData<T> input =
                JsonMapperUtils.fromJson(data, MessageData.class, this.getMessageContentType());

        if (input == null) {
            log.info("[KafkaConsumer][{}][{}][{}]  ignore!", topic, partition, offset);
            acknowledgment.acknowledge(); // vẫn commit offset nếu message lỗi parse
            ThreadContext.clearAll();
            return;
        }

        // Nếu messageId trống thì generate theo topic-partition-offset
        if (StringUtils.isBlank(input.getMessageId())) {
            input.updateMessageId(String.format("%s_%s_%s", topic, partition, offset));
        }

        try {
            this.handleMessageEvent(topic, partition, offset, input);
        } catch (Exception e) {
            log.error("[KafkaConsumer][{}][{}][{}]  Exception revert ", topic, partition, offset, e);
            // Có thể mở rộng thêm cơ chế đẩy vào DLQ ở đây
        } finally {
            acknowledgment.acknowledge(); // commit offset sau khi xử lý xong
            ThreadContext.clearAll();     // dọn dẹp MDC context
        }
    }

    /**
     * Khởi tạo các thông tin logging context và log thông tin message nhận được.
     */
    private void initListener(String topic, String partition, String offset, String data) {
        ThreadContext.put("X-Request-ID", UUID.randomUUID().toString()); // dùng cho trace log
        ThreadContext.put("EventBrokerType", "KAFKA");
        ThreadContext.put("MessageListenerEvent", topic);
        log.info("[KafkaConsumer][{}][{}][{}] Incoming: {}", topic, partition, offset, data);
    }

    /**
     * Abstract method bắt buộc các class con implement để xử lý message thực tế.
     */
    protected abstract void handleMessageEvent(
            String topic, String partition, String offset, MessageData<T> input);

    /**
     * Dùng reflection để lấy ra generic type T ở runtime.
     * Cảnh báo: sẽ lỗi nếu class này bị proxy bởi Spring mà không giữ lại generic type.
     *
     * @return class của generic type T.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Class getMessageContentType() {
        return (Class<T>)
                ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
