package com.algaworks.algashop.ordering.core.application.order.event;

import com.algaworks.algashop.ordering.core.application.order.snapshot.OrderItemSnapshot;
import com.algaworks.algashop.ordering.core.domain.model.order.Order;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderItem;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Objects;

@Component
public class OrderPaidIntegrationEventAssembler {

    public OrderPaidIntegrationEvent toIntegrationEvent(Order order) {
        Objects.requireNonNull(order);

        return OrderPaidIntegrationEvent.builder()
                .orderId(order.id().toString())
                .customerId(order.customerId().value())
                .paidAt(order.paidAt())
                .items(order.items().stream()
                        .sorted(Comparator.comparing(item -> item.id().toString()))
                        .map(this::toOrderItemSnapshot)
                        .toList())
                .build();
    }

    private OrderItemSnapshot toOrderItemSnapshot(OrderItem item) {
        return new OrderItemSnapshot(
                item.orderId().toString(),
                item.productId().value(),
                item.productName().value(),
                item.price().value(),
                item.quantity().value(),
                item.totalAmount().value());
    }
}