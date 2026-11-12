package com.algaworks.algashop.ordering.infrastructure.config.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductCacheManager {

	public static final String PRODUCT_CATALOG_API_CACHE_NAME = "algashop:product-catalog-api:v1";

	private final CacheManager cacheManager;

	public void evict(UUID productId) {
		Optional.ofNullable(cacheManager.getCache(PRODUCT_CATALOG_API_CACHE_NAME))
				.ifPresent(cache -> cache.evictIfPresent(productId));
	}

}
