package com.line.line_demo.config.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.IsolationLevel;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.DefaultKafkaHeaderMapper;
import org.springframework.kafka.support.KafkaHeaderMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
/**
 * Cấu hình Kafka cho hệ thống.
 * Bao gồm consumer factory, producer factory, thread pool executor, và Kafka listener container.
 * Được inject tự động từ cấu hình trong application.yml/properties.
 */
public class KafkaConfig {

    // Địa chỉ Kafka broker (có thể là nhiều broker phân tách bằng dấu phẩy)
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServerUrl;

    // Cho phép listener xử lý nhiều message cùng lúc (batch listener)
    @Value("${spring.kafka.messaging.kafka.consumer.batch}")
    private boolean isBatchConsumerNapasTransfer;

    // Thời gian timeout khi polling message (ms) – để tránh consumer bị considered là chết
    @Value("${spring.kafka.consumer.max.timeout}")
    private int consumerTimeout;

    // Số lượng message tối đa trong mỗi lần batch – đừng set quá to kẻo memory bay màu
    @Value("${spring.kafka.messaging.kafka.consumer.number.of.message.in.batch}")
    private int maxBatchRecordNapasTransfer;

    // Số lượng thread xử lý message song song – scale tùy CPU, nhưng đừng overcommit
    @Value("${spring.kafka.messaging.consumer.pool.size}")
    private int kafkaConsumerThreadPoolSize;

    // Thời gian chờ tối đa khi hệ thống shutdown để thread pool xử lý nốt job đang chạy (giây)
    @Value("${spring.kafka.graceful.shutdown.messaging.consumer.wait.time.max}")
    private int waitTimeMax;

    // Prefix cho tên thread – dùng để debug log hoặc trace dễ hơn
    @Value("${spring.kafka.messaging.consumer.pool.thread.name.prefix}")
    private String threadNamePrefix;

    /**
     * Tạo thread pool executor riêng cho consumer – tránh block main thread hoặc dùng chung executor.
     */
    @Bean(name = "kafkaConsumerThreadPool")
    public ThreadPoolTaskExecutor kafkaConsumerThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(kafkaConsumerThreadPoolSize);
        executor.setMaxPoolSize(kafkaConsumerThreadPoolSize);
        executor.setAllowCoreThreadTimeOut(true); // Giải phóng thread khi nhàn rỗi
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(waitTimeMax);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }

    /**
     * Factory tạo Kafka Consumer – có thể customize max poll records, commit, v.v...
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerUrl);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxBatchRecordNapasTransfer);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // Manual commit để tránh mất dữ liệu
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, IsolationLevel.READ_COMMITTED.toString().toLowerCase());
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, consumerTimeout);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * Kafka Listener Container Factory – định nghĩa cách listener hoạt động
     */
    @Bean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        // Cho phép xử lý batch message (nhiều message 1 lần)
        factory.setBatchListener(isBatchConsumerNapasTransfer);

        // Manual commit ngay lập tức sau khi xử lý xong
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        // Gán thread pool riêng để xử lý message
        factory.getContainerProperties().setListenerTaskExecutor(kafkaConsumerThreadPool());

        return factory;
    }

    /**
     * Dùng để ánh xạ custom header trong Kafka message – thường dùng trong Spring Cloud Stream
     */
    @Bean(name = "kafkaBinderHeaderMapper")
    public KafkaHeaderMapper kafkaBinderHeaderMapper() {
        DefaultKafkaHeaderMapper mapper = new DefaultKafkaHeaderMapper();
        mapper.setMapAllStringsOut(true); // Tự động convert tất cả header về String
        return mapper;
    }

    /**
     * Factory tạo Kafka Producer – cấu hình đơn giản chỉ gửi chuỗi
     */
    @Bean
    public ProducerFactory<Object, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerUrl);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false); // Không đảm bảo duy nhất – phù hợp với tốc độ
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * KafkaTemplate – để gửi message đến Kafka
     */
    @Bean
    public KafkaTemplate<Object, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}

