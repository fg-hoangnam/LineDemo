package com.line.line_demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


/**
 * Cấu hình thread pool executor cho các tác vụ xử lý bất đồng bộ (@Async).
 * Dành riêng cho các tác vụ gửi dữ liệu (ví dụ: gửi message lên LINE, gọi API, v.v...).
 */
@EnableAsync
@Configuration
public class AsyncConfig {

    @Value("${spring.threads.pool-core-size}")
    private int POOL_CORE_SIZE;

    @Value("${spring.threads.max-pool-size}")
    private int MAX_POOL_SIZE;

    @Value("${spring.threads.queue-capacity}")
    private int QUEUE_CAPACITY;

    @Value("${spring.threads.name-prefix}")
    private String PREFIX;


    /**
     * Tạo bean executor đặt tên là "taskExecutor" – mặc định được Spring @Async sử dụng nếu không chỉ định.
     *
     * Cấu hình thread pool:
     * - Core pool: 10 thread – số lượng tối thiểu luôn tồn tại.
     * - Max pool: 20 thread – giới hạn tối đa nếu workload tăng.
     * - Queue capacity: 500 – tối đa 500 task được xếp hàng đợi.
     * - Thread name prefix: "Send-Data-Line-" – để dễ trace log và debug thread nào làm gì.
     *
     * ⚠️ Chú ý:
     * - Nếu queue đầy và max pool đạt ngưỡng, task mới sẽ bị từ chối (RejectedExecutionException).
     * - Cần bổ sung `RejectedExecutionHandler` nếu muốn graceful degradation (log lại, retry, v.v...).
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(POOL_CORE_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setThreadNamePrefix(PREFIX);
        executor.initialize();
        return executor;
    }
}

