package com.algaworks.algashop.product.catalog.domain.model.stock;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface StockReservationRepository extends MongoRepository<StockReservation, UUID> {
	boolean existsByOrderId(String orderId);
}
