package com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.order.saga;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlaceOrderSagaPersistenceEntityRepository
		extends JpaRepository<PlaceOrderSagaPersistenceEntity, UUID> {
}
