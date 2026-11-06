package com.algaworks.algashop.ordering.core.application.order.snapshot;

import com.algaworks.algashop.ordering.core.domain.model.commons.Address;
import com.algaworks.algashop.ordering.core.domain.model.order.Billing;
import com.algaworks.algashop.ordering.core.domain.model.order.Order;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderItem;
import com.algaworks.algashop.ordering.core.domain.model.order.Shipping;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderSnapshotAssembler {

    public OrderSnapshot toSnapshot(Order order) {
        return new OrderSnapshot(
                order.id().toString(),
                order.customerId().value(),
                order.placedAt(),
                order.status(),
                toSnapshot(order.billing()),
                toSnapshot(order.shipping()),
                order.paymentMethod().name(),
                order.creditCardId() == null ? null : order.creditCardId().id(),
                order.totalAmount().value(),
                order.totalItems().value(),
                order.items().stream().map(this::toSnapshot).collect(Collectors.toSet())
        );
    }

    private BillingSnapshot toSnapshot(Billing billing) {
        return new BillingSnapshot(
                billing.fullName().firstName(),
                billing.fullName().lastName(),
                billing.document().value(),
                billing.email().value(),
                billing.phone().value(),
                toSnapshot(billing.address())
        );
    }

    private ShippingSnapshot toSnapshot(Shipping shipping) {
        var recipient = shipping.recipient();
        return new ShippingSnapshot(
                shipping.cost().value(), shipping.expectedDate(),
                new RecipientSnapshot(
                        recipient.fullName().firstName(), recipient.fullName().lastName(),
                        recipient.document().value(), recipient.phone().value()),
                toSnapshot(shipping.address())
        );
    }

    private AddressSnapshot toSnapshot(Address address) {
        return new AddressSnapshot(
                address.street(),
                address.number(),
                address.complement(),
                address.neighborhood(),
                address.city(),
                address.state(),
                address.zipCode().value());
    }

    private OrderItemSnapshot toSnapshot(OrderItem item) {
        return new OrderItemSnapshot(item.id().toString(),
                item.productId().value(),
                item.productName().value(),
                item.price().value(),
                item.quantity().value(),
                item.totalAmount().value());
    }
}
