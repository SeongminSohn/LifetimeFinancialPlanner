package com.app.lifetimefinancialplanner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync // necessary for multithreading
public class AsyncConfig {

    @Bean("simExecutor") // pool for simualation
    public Executor simExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(Runtime.getRuntime().availableProcessors()); // 코어 수
        exec.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 2);
        exec.setQueueCapacity(100);
        exec.setThreadNamePrefix("sim-");
        exec.initialize();
        return exec;
    }
}
