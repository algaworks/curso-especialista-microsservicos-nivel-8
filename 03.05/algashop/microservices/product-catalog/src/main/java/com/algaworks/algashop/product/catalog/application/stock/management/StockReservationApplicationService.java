package com.algaworks.algashop.product.catalog.application.stock.management;

import com.algaworks.algashop.product.catalog.domain.model.product.ProductNotFoundException;
import com.algaworks.algashop.product.catalog.domain.model.stock.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockReservationApplicationService {

	private final StockService stockService;
	private final StockMovementRepository stockMovementRepository;
	private final StockReservationRepository stockReservationRepository;

	@Transactional
	public UUID reserveForOrder(StockReservationInput input) {
		Objects.requireNonNull(input);

		String orderId = input.getOrderId();
		assertReservationDoesNotExists(orderId);

		List<StockReservationItem> items = toItems(input);

		List<StockMovement> movements;
		StockReservation reservation;

		try {
			movements = stockService.withdrawAll(orderId, items);
			reservation = StockReservation.confirmed(orderId, items);
		} catch (InsufficientStockException | ProductNotFoundException e) {
			movements = List.of();
			reservation = StockReservation.rejected(orderId, items);
		}

		if (!movements.isEmpty()) {
			stockMovementRepository.saveAll(movements);
		}
		stockReservationRepository.save(reservation);

		return reservation.getId();
	}

	private List<StockReservationItem> toItems(StockReservationInput input) {
		return input.getItems().stream().map(item -> StockReservationItem.builder()
				.productId(item.getProductId())
				.quantity(item.getQuantity())
				.build()).toList();
	}

	private void assertReservationDoesNotExists(String orderId) {
		if (stockReservationRepository.existsByOrderId(orderId)) {
			throw new DuplicateStockReservationException(orderId);
		}
	}

}
