package com.algaworks.algashop.ordering.core.application.product.event;

import com.algaworks.algashop.ordering.core.application.InboundIntegrationEvent;
import com.algaworks.algashop.ordering.core.application.OutboundIntegrationEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductPriceChangedIntegrationEvent implements InboundIntegrationEvent {
	private UUID productId;
	private OffsetDateTime changedAt;
}
