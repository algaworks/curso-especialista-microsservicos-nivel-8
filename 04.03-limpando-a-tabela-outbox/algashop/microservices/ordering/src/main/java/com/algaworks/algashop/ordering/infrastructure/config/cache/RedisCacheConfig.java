package com.algaworks.algashop.ordering.infrastructure.config.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.time.Duration;

import static com.algaworks.algashop.ordering.infrastructure.config.cache.ProductCacheManager.PRODUCT_CATALOG_API_CACHE_NAME;

@Configuration
@EnableCaching
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
public class RedisCacheConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        var defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .computePrefixWith(c -> c + ":")
                .entryTtl(Duration.ofMinutes(1));
        return (builder) -> builder.cacheDefaults(defaultCacheConfig)
                .withCacheConfiguration(PRODUCT_CATALOG_API_CACHE_NAME,
                        defaultCacheConfig.disableCachingNullValues().entryTtl(Duration.ofMinutes(1)));
    }

}