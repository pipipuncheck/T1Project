package org.example.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class CacheConfig {

    @Bean(destroyMethod = "shutdown")
    public ScheduledExecutorService cacheCleanupExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }
}
