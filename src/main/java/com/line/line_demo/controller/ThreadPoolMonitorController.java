package com.line.line_demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/thread-pool-monitor")
public class ThreadPoolMonitorController {

    private final ThreadPoolTaskExecutor taskExecutor;

    @GetMapping("/thread-pool-stats")
    public Map<String, Object> stats() {
        ThreadPoolExecutor exec = taskExecutor.getThreadPoolExecutor();
        return Map.of(
                "Active", exec.getActiveCount(),
                "PoolSize", exec.getPoolSize(),
                "QueueSize", exec.getQueue().size(),
                "Completed", exec.getCompletedTaskCount()
        );
    }
}
