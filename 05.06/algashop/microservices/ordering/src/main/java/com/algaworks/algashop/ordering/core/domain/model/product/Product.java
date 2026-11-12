package com.algaworks.algashop.ordering.core.domain.model.product;

import com.algaworks.algashop.ordering.core.domain.model.commons.Money;
import com.algaworks.algashop.ordering.core.domain.model.commons.Quantity;
import lombok.Builder;

import java.util.Objects;

@Builder
public record Product(
        ProductId id,
        ProductName name,
        Money price,
        Boolean inStock,
        Quantity quantityInStock
) {
    public Product {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        Objects.requireNonNull(price);
        Objects.requireNonNull(inStock);
    }

    public void checkOutOfStock() {
        if (isOutOfStock()) {
            throw new ProductOutOfStockException(this.id());
        }
    }

    public void checkForQuantity(Quantity quantity) {
        Objects.requireNonNull(quantity);
        checkOutOfStock();
        if (this.quantityInStock() != null && this.quantityInStock().compareTo(quantity) < 0) {
            throw new InsufficientQuantityException(this.name(), quantity);
        }
    }

    private boolean isOutOfStock() {
        return !inStock();
    }
}
