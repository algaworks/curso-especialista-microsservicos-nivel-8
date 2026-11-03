package com.algaworks.algashop.ordering.infrastructure.adapters.in.messaging.kafka.product;

import com.algaworks.algashop.ordering.core.application.product.event.ProductDelistedIntegrationEvent;
import com.algaworks.algashop.ordering.core.application.product.event.ProductListedIntegrationEvent;
import com.algaworks.algashop.ordering.core.application.product.event.ProductPriceChangedV2IntegrationEvent;
import com.algaworks.algashop.ordering.core.application.product.event.ProductRestockedIntegrationEvent;
import com.algaworks.algashop.ordering.core.application.product.event.ProductSoldOutIntegrationEvent;
import com.algaworks.algashop.ordering.core.domain.model.DomainException;
import com.algaworks.algashop.ordering.core.ports.in.shoppingcart.ForManagingShoppingCarts;
import com.algaworks.algashop.ordering.core.ports.out.idempotency.ForGuardingIdempotency;
import com.algaworks.algashop.ordering.infrastructure.config.cache.ProductCacheManager;
import com.algaworks.algashop.ordering.infrastructure.config.kafka.KafkaConfig;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

@Component
@Slf4j
@KafkaListener(
		id = "#{algaShopMessagingKafkaProperties.productEventsConsumerGroup}",
		concurrency = "3",
		topics = {"#{algaShopMessagingKafkaProperties.productEventTopicName}"}
)
@RequiredArgsConstructor
public class KafkaProductIntegrationEventListener {

	private final ForManagingShoppingCarts forManagingShoppingCarts;
	private final ProductCacheManager productCacheManager;
	private final ForGuardingIdempotency forGuardingIdempotency;

	@Value("${simulate:none}") // none | slow | technical | business
	private String simulate;

	@KafkaHandler(isDefault = true)
	public void handle(@Payload Object event,
	                   @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
	                   @Header(value = KafkaHeaders.OFFSET, required = false) String messageOffset
	                   ) {
		log.info("Event ignored: key={} offset={}", messageKey, messageOffset);
//		simulateProcessing();
	}

	@KafkaHandler
	public void handle(@Payload ProductListedIntegrationEvent event,
	                   @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
	                   @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
	                   @Header(value = KafkaHeaders.OFFSET, required = false) Integer offset) {
		logReceived(event, messageKey, partition, offset);
//		simulateProcessing();
		productCacheManager.evict(event.getProductId());
		forManagingShoppingCarts.changeProductAvailability(event.getProductId(), true);
	}

	@KafkaHandler
	public void handle(@Payload ProductDelistedIntegrationEvent event,
	                   @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
	                   @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
	                   @Header(value = KafkaHeaders.OFFSET, required = false) Integer offset) {
		logReceived(event, messageKey, partition, offset);
//		simulateProcessing();
		productCacheManager.evict(event.getProductId());
		forManagingShoppingCarts.changeProductAvailability(event.getProductId(), false);
	}

	@KafkaHandler
	public void handle(@Payload ProductRestockedIntegrationEvent event,
	                   @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
	                   @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
	                   @Header(value = KafkaHeaders.OFFSET, required = false) Integer offset) {
		logReceived(event, messageKey, partition, offset);
		productCacheManager.evict(event.getProductId());
		forManagingShoppingCarts.changeProductAvailability(event.getProductId(), true);
	}

	@KafkaHandler
	public void handle(@Payload ProductSoldOutIntegrationEvent event,
	                   @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
	                   @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
	                   @Header(value = KafkaHeaders.OFFSET, required = false) Integer offset) {
		logReceived(event, messageKey, partition, offset);
		productCacheManager.evict(event.getProductId());
		forManagingShoppingCarts.changeProductAvailability(event.getProductId(), false);
	}

	@KafkaHandler
	public void handle(@Payload @Valid ProductPriceChangedV2IntegrationEvent event,
	                   @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
	                   @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
	                   @Header(value = KafkaHeaders.OFFSET, required = false) Integer offset,
	                   @Header(value = KafkaConfig.IDEMPOTENCY_KEY_HEADER) byte[] rawIdempotencyKey) {
		logReceived(event, messageKey, partition, offset);

		UUID idempotencyKey = UUID.fromString(new String(rawIdempotencyKey, StandardCharsets.UTF_8));

		boolean processed = forGuardingIdempotency.runOnce(idempotencyKey, () -> {
			productCacheManager.evict(event.getProductId()); //Redis não tem rollback
			forManagingShoppingCarts.refreshProductPrice(event.getProductId(), event.getNewSalePrice()); //rollback
			simulateProcessing();
		});

		if (!processed) {
			return;
		}

		log.warn("Mail send product price on shopping cart was been updated.");
	}

	private void simulateProcessing() {
		switch (simulate) {
			case "slow" -> {
				log.warn("Simulate: Slow call");
				try {
					Thread.sleep(Duration.ofSeconds(90));
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			case "technical" -> throw new RuntimeException("Simulated failure: database is down");
			case "business" -> throw new DomainException("Simulated business: rule violation");
		}
	}

	private void logReceived(Object event, String messageKey, Integer partition, Integer offset) {
		log.info("Received {} | key={} | partiton={} | offset={} | thread={}",
				event.getClass().getSimpleName(), messageKey, partition, offset,
				Thread.currentThread().getName());
	}

}
