package com.algaworks.algashop.ordering.core.application.product.event;

import com.algaworks.algashop.ordering.core.application.InboundIntegrationEvent;
import com.algaworks.algashop.ordering.core.application.OutboundIntegrationEvent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductPriceChangedV2IntegrationEvent implements InboundIntegrationEvent {
	@NotNull
	private UUID productId;
	@NotNull
	private OffsetDateTime changedAt;
	@NotNull
	private BigDecimal oldRegularPrice;
	@NotNull
	private BigDecimal oldSalePrice;
	@NotNull
	private BigDecimal newRegularPrice;
	@NotNull
	private BigDecimal newSalePrice;

}
