package com.algaworks.algashop.product.catalog.infrastructure.listener.stock;

import com.algaworks.algashop.product.catalog.domain.model.DomainEventPublisher;
import com.algaworks.algashop.product.catalog.domain.model.product.ProductRestockedEvent;
import com.algaworks.algashop.product.catalog.domain.model.product.ProductSoldOutEvent;
import com.algaworks.algashop.product.catalog.domain.model.stock.StockMovementRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class StockMovementEventListener {

	private final DomainEventPublisher domainEventPublisher;

	@EventListener
	public void handle(StockMovementRegisteredEvent event) {
		log.info("StockMovementRegisteredEvent {}", event);

		if (event.causedSoldOut()) {
			domainEventPublisher.publish(ProductSoldOutEvent.builder()
					.productId(event.getProductId())
					.build());
		}

		if (event.causedRestock()) {
			domainEventPublisher.publish(ProductRestockedEvent.builder()
					.productId(event.getProductId())
					.build());
		}
	}

	@TransactionalEventListener
	@CacheEvict(cacheNames = "algashop:products:v1", key = "#event.productId")
	public void handleEvictProductCache(StockMovementRegisteredEvent event) {
		log.info("StockMovementRegisteredEvent handle product {} cache evict", event.getProductId());
	}

}
