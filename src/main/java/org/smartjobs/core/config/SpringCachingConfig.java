package org.smartjobs.core.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class SpringCachingConfig implements CachingConfigurer {


    private final int initialSize;
    private final int maxSize;
    private final int expireAfterAccess;
    private final int expireAfterWrite;


    public SpringCachingConfig(@Value("${cache.general.initial-size}") int initialSize,
                               @Value("${cache.general.max-size}") int maxSize,
                               @Value("${cache.general.expire-minutes-after-access}") int expireAfterAccess,
                               @Value("${cache.static.expire-minutes-after-write}") int expireAfterWrite) {
        this.initialSize = initialSize;
        this.maxSize = maxSize;
        this.expireAfterAccess = expireAfterAccess;
        this.expireAfterWrite = expireAfterWrite;
    }


    @Bean
    @Override
    public CacheManager cacheManager() {
        var cacheManager = new CaffeineCacheManager(
                "cv-name",
                "role",
                "current-role",
                "cv-currently-selected",
                "role-display"
        );
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(initialSize)
                .maximumSize(maxSize)
                .expireAfterAccess(Duration.ofMinutes(expireAfterAccess)));
        return cacheManager;
    }

    @Bean
    public CacheManager staticCacheManager(
    ) {
        var cacheManager = new CaffeineCacheManager(
                "defined-criteria"
        );
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(1)
                .maximumSize(1)
                .expireAfterWrite(Duration.ofMinutes(expireAfterWrite)));
        return cacheManager;
    }
}