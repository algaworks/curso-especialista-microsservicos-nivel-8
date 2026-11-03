package com.algaworks.algashop.product.catalog.domain.model.stock;

import com.algaworks.algashop.product.catalog.domain.model.DomainException;
import com.algaworks.algashop.product.catalog.domain.model.product.Product;
import com.algaworks.algashop.product.catalog.domain.model.product.ProductNotFoundException;
import com.algaworks.algashop.product.catalog.domain.model.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final QuantityInStockAdjustment quantityInStockAdjustment;
    private final ProductRepository productRepository;

    public StockMovement restock(UUID productId, int quantity) {
        Objects.requireNonNull(productId);
        if (quantity < 1) {
            throw new IllegalArgumentException();
        }

        QuantityInStockAdjustment.Result result = quantityInStockAdjustment.increase(productId, quantity);;

        return StockMovement.builder()
                .productId(productId)
                .movementQuantity(quantity)
                .previousQuantity(result.previousQuantity())
                .newQuantity(result.newQuantity())
                .type(StockMovement.MovementType.STOCK_IN)
                .build();
    }

    public StockMovement withdraw(UUID productId, int quantity, String orderId) {
        Objects.requireNonNull(productId);
        if (quantity <1) {
            throw new IllegalArgumentException();
        }

        QuantityInStockAdjustment.Result result = quantityInStockAdjustment.decrease(productId, quantity);

        return StockMovement.builder()
                .productId(productId)
                .movementQuantity(quantity)
                .previousQuantity(result.previousQuantity())
                .newQuantity(result.newQuantity())
                .type(StockMovement.MovementType.STOCK_OUT)
                .orderId(orderId)
                .build();
    }

    public List<StockMovement> withdrawAll(String orderId, List<StockReservationItem> items) {

        Map<UUID, Integer> quantityByProduct = items.stream().collect(
                Collectors.groupingBy(
                        StockReservationItem::getProductId,
                        Collectors.summingInt(StockReservationItem::getQuantity)
                )
        );

        Map<UUID, Product> productById = productRepository.findAllById(quantityByProduct.keySet())
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        quantityByProduct.forEach((productId, quantity) -> {
            Product product = productById.get(productId);

            if (product == null) {
                throw new ProductNotFoundException(productId);
            }

            if (!product.hasQuantity(quantity)) {
                throw new InsufficientStockException(productId, quantity, product.getQuantityInStock());
            }
        });

        return quantityByProduct.entrySet().stream()
                .map(entry -> withdraw(entry.getKey(), entry.getValue(), orderId))
                .toList();
    }

}
