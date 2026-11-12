package com.algaworks.algashop.ordering.core.application.order.event;

import com.algaworks.algashop.ordering.core.application.order.snapshot.*;
import com.algaworks.algashop.ordering.core.domain.model.commons.Address;
import com.algaworks.algashop.ordering.core.domain.model.order.*;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Objects;

@Component
public class OrderPlacedIntegrationEventAssembler {  
  
    public OrderPlacedIntegrationEvent toIntegrationEvent(Order order) {  
        Objects.requireNonNull(order);  
  
        return OrderPlacedIntegrationEvent.builder()  
                .orderId(order.id().toString())  
                .customerId(order.customerId().value())  
                .placedAt(order.placedAt())  
                .totalAmount(order.totalAmount().value())  
                .items(order.items().stream()  
                        .sorted(Comparator.comparing(item -> item.id().toString()))  
                        .map(this::toOrderItemData)  
                        .toList())  
                .payment(new PaymentSnapshot(  
                        order.paymentMethod().name(),  
                        order.creditCardId() == null ? null : order.creditCardId().id()))  
                .shipping(toShippingData(order.shipping()))  
                .billing(toBillingData(order.billing()))  
                .build();  
    }  
  
    private OrderItemSnapshot toOrderItemData(OrderItem item) {  
        return new OrderItemSnapshot(  
                item.orderId().toString(),
                item.productId().value(),  
                item.productName().value(),  
                item.price().value(),  
                item.quantity().value(),  
                item.totalAmount().value());  
    }  
  
    private ShippingSnapshot toShippingData(Shipping shipping) {  
        return new ShippingSnapshot(  
                shipping.cost().value(),  
                shipping.expectedDate(),  
                toRecipientData(shipping.recipient()),  
                toAddressData(shipping.address()));  
    }  
  
    private RecipientSnapshot toRecipientData(Recipient recipient) {  
        return new RecipientSnapshot(  
                recipient.fullName().firstName(),  
                recipient.fullName().lastName(),  
                recipient.document().value(),  
                recipient.phone().value());  
    }  
  
    private BillingSnapshot toBillingData(Billing billing) {  
        return new BillingSnapshot(  
                billing.fullName().firstName(),  
                billing.fullName().lastName(),  
                billing.document().value(),  
                billing.email().value(),  
                billing.phone().value(),  
                toAddressData(billing.address()));  
    }  
  
    private AddressSnapshot toAddressData(Address address) {  
        return new AddressSnapshot(  
                address.street(),  
                address.number(),  
                address.complement(),  
                address.neighborhood(),  
                address.city(),  
                address.state(),  
                address.zipCode().value());  
    }
}