package dev.codefortress.web.service;

import dev.codefortress.core.config.CodeFortressProperties;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for rate limiting requests based on IP address.
 * It uses an in-memory map to store buckets for each IP.
 */
@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    private final CodeFortressProperties properties;

    /**
     * Resolves the bucket for a given IP address.
     * If a bucket for the IP does not exist, a new one is created.
     *
     * @param ip the IP address to resolve the bucket for
     * @return the bucket for the given IP address
     */
    public Bucket resolveBucket(String ip) {
        return cache.computeIfAbsent(ip, this::newBucket);
    }

    private Bucket newBucket(String ip) {
        CodeFortressProperties.RateLimit config = properties.getRateLimit();
        Bandwidth limit = Bandwidth.classic(
                config.getMaxAttempts(),
                Refill.greedy(config.getMaxAttempts(), Duration.ofSeconds(config.getDurationSeconds()))
        );
        return Bucket.builder().addLimit(limit).build();
    }
}