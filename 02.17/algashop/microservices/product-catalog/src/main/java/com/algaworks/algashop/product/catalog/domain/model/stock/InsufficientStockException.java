package com.algaworks.algashop.product.catalog.domain.model.stock;

import com.algaworks.algashop.product.catalog.domain.model.DomainException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class InsufficientStockException extends DomainException {

    private final UUID productId;
    private final int requested;
    private final int available;

    public InsufficientStockException(UUID productId, int requested, int available) {
        super(String.format("Product %s has %d units in stock, %d requested",
                productId, available, requested));
        this.productId = productId;
        this.requested = requested;
        this.available = available;
    }
}