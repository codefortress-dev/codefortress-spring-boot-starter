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

@Service
@RequiredArgsConstructor
public class RateLimitService {

    // Almacenamos los buckets en memoria (IP -> Bucket)
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    private final CodeFortressProperties properties;

    public Bucket resolveBucket(String ip) {
        return cache.computeIfAbsent(ip, this::newBucket);
    }

    private Bucket newBucket(String ip) {
        CodeFortressProperties.RateLimit config = properties.getRateLimit();

        // Usamos los valores configurados por el usuario
        // Nota: Aquí mantenemos 'greedy' por ser mejor UX, pero podrías parametrizar esto también si quisieras
        Bandwidth limit = Bandwidth.classic(
                config.getMaxAttempts(),
                Refill.greedy(config.getMaxAttempts(), Duration.ofSeconds(config.getDurationSeconds()))
        );

        return Bucket.builder().addLimit(limit).build();
    }
}