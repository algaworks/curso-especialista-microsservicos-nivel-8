package com.algaworks.algashop.product.catalog.domain.model.stock;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface StockMovementRepository extends MongoRepository<StockMovement, UUID> {
}
