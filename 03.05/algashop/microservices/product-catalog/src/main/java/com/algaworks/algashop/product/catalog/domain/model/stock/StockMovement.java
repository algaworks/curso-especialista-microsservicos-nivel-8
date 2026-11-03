package com.algaworks.algashop.product.catalog.domain.model.stock;

import com.algaworks.algashop.product.catalog.domain.model.IdGenerator;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;
import java.util.UUID;

@Document(collection = "stock_movements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class StockMovement extends AbstractAggregateRoot<StockMovement> {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;
    private OffsetDateTime occurredAt;
    private UUID productId;
    private Integer movementQuantity;
    private Integer previousQuantity;
    private Integer newQuantity;
    private MovementType type;

    private String orderId;

    @Builder
    public StockMovement(UUID productId,
                         Integer movementQuantity,
                         Integer previousQuantity,
                         Integer newQuantity,
                         MovementType type,
                         String orderId) {
        this.id = IdGenerator.generateTimeBasedUUID();
        this.occurredAt = OffsetDateTime.now();

        this.productId = productId;
        this.movementQuantity = movementQuantity;
        this.previousQuantity = previousQuantity;
        this.newQuantity = newQuantity;
        this.type = type;
        this.orderId = orderId;

        super.registerEvent(StockMovementRegisteredEvent.builder()
                .movementId(this.id)
                .productId(this.productId)
                .type(this.type)
                .movementQuantity(this.movementQuantity)
                .previousQuantity(this.previousQuantity)
                .newQuantity(this.newQuantity)
                .occurredAt(this.occurredAt)
                .orderId(this.orderId)
                .build());
    }

    public enum MovementType {
        STOCK_IN,
        STOCK_OUT
    }
}
