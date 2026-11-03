package com.algaworks.algashop.ordering.core.application.invoice.command;

import com.algaworks.algashop.ordering.core.application.OutboundIntegrationCommand;
import com.algaworks.algashop.ordering.core.application.order.snapshot.BillingSnapshot;
import com.algaworks.algashop.ordering.core.application.order.snapshot.OrderItemSnapshot;
import com.algaworks.algashop.ordering.core.application.order.snapshot.OrderSnapshot;
import com.algaworks.algashop.ordering.core.application.order.snapshot.PaymentSnapshot;
import com.algaworks.algashop.ordering.core.application.order.snapshot.ShippingSnapshot;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssueInvoiceIntegrationCommand implements OutboundIntegrationCommand {

    @NotBlank
    private String orderId;

    @NotNull
    private UUID customerId;

    @NotNull
    private OffsetDateTime placedAt;

    @NotNull
    @Positive
    private BigDecimal totalAmount;

    @Builder.Default
    @NotNull
    @Size(min = 1)
    @Valid
    private List<OrderItemSnapshot> items = new ArrayList<>();

    @NotNull
    @Valid
    private PaymentSnapshot payment;

    @NotNull
    @Valid
    private ShippingSnapshot shipping;

    @NotNull
    @Valid
    private BillingSnapshot billing;

    public static IssueInvoiceIntegrationCommand from(OrderSnapshot order) {
        return IssueInvoiceIntegrationCommand.builder()
                .orderId(order.orderId())
                .customerId(order.customerId())
                .placedAt(order.placedAt())
                .totalAmount(order.totalAmount())
                .items(order.items().stream()
                        .sorted(Comparator.comparing(OrderItemSnapshot::orderItemId))
                        .toList())
                .payment(new PaymentSnapshot(order.paymentMethod(), order.creditCardId()))
                .shipping(order.shipping())
                .billing(order.billing())
                .build();
    }

    @Override
    public String getAggregateId() {
        return orderId;
    }
}