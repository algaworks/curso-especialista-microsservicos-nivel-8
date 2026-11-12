package com.algaworks.algashop.ordering.core.application.order.snapshot;

import com.algaworks.algashop.ordering.core.domain.model.order.OrderStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record OrderSnapshot(
        String orderId,
        UUID customerId,
        OffsetDateTime placedAt,
        OrderStatus status,
        BillingSnapshot billing,
        ShippingSnapshot shipping,
        String paymentMethod,
        UUID creditCardId,
        BigDecimal totalAmount,
        Integer totalItems,
        Set<OrderItemSnapshot> items
){}
