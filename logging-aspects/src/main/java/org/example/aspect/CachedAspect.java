package org.example.aspect;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.annotation.Cached;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class CachedAspect {

    @Value("${cache.ttl-seconds:60}")
    private long ttlSeconds;

    private final ScheduledExecutorService cacheCleanupExecutor;

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    private record CacheEntry(Object value, long expireAt) {}

    @PostConstruct
    public void scheduleCleanup() {
        cacheCleanupExecutor.scheduleAtFixedRate(this::cleanup, 1, 10, TimeUnit.SECONDS);
    }

    @Around("@annotation(cached)")
    public Object cacheMethod(ProceedingJoinPoint joinPoint, Cached cached) throws Throwable {
        String key = buildCacheKey(joinPoint, cached.cacheName());

        CacheEntry entry = cache.get(key);
        if (entry != null && System.currentTimeMillis() < entry.expireAt()) {
            log.info("[Cached] Hit: {}", key);
            return entry.value();
        }

        Object result = joinPoint.proceed();

        cache.put(key, new CacheEntry(result, System.currentTimeMillis() + ttlSeconds * 1000));
        log.info("[Cached] Stored key={} ttl={}s", key, ttlSeconds);

        return result;
    }

    private String buildCacheKey(ProceedingJoinPoint joinPoint, String cacheName) {
        String base = cacheName.isEmpty()
                ? joinPoint.getSignature().toShortString()
                : cacheName;
        Object[] args = joinPoint.getArgs();
        return base + "_" + Arrays.hashCode(args);
    }

    private void cleanup() {
        long now = System.currentTimeMillis();
        cache.entrySet().removeIf(e -> e.getValue().expireAt() < now);
    }
}
