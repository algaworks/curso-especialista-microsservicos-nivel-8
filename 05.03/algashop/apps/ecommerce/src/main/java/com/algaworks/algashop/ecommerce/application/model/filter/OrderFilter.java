package com.algaworks.algashop.ecommerce.application.model.filter;

import com.algaworks.algashop.ecommerce.application.model.client.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Iterator;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderFilter {
	private OrderStatus status;
	private OffsetDateTime placedAtFrom;
	private OffsetDateTime placedAtTo;
	private BigDecimal totalAmountFrom;
	private BigDecimal totalAmountTo;
	private String orderId;

	private int size;
	private int page;

	@Builder.Default
	@JsonIgnore
	private SortType sort = SortType.PLACE_AT;

	@Builder.Default
	@JsonIgnore
	private Sort.Direction sortDirection = Sort.Direction.DESC;

	public static OrderFilter of(Pageable pageable) {
		OrderFilterBuilder builder = OrderFilter.builder()
				.page(pageable.getPageNumber())
				.size(pageable.getPageSize());

		Iterator<Sort.Order> iterator = pageable.getSort().stream().iterator();

		if (iterator.hasNext()) {
			Sort.Order order = iterator.next();
			builder.sort(SortType.valueOf(order.getProperty()));
			builder.sortDirection(order.getDirection());
		}

		return builder.build();
	}

	@Getter
	@AllArgsConstructor
	enum SortType {
		PLACE_AT("placedAt");

		private final String propertyName;
	}

}
