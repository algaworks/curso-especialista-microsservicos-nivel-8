package com.algaworks.algashop.ordering.core.application.product.event;

import com.algaworks.algashop.ordering.core.application.InboundIntegrationEvent;
import com.algaworks.algashop.ordering.core.application.OutboundIntegrationEvent;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListedIntegrationEvent implements InboundIntegrationEvent {
	private UUID productId;
	private OffsetDateTime listedAt;
}