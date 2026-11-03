package com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.order;

import com.algaworks.algashop.ordering.core.domain.model.IdGenerator;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.commons.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.order.OrderPersistenceEntity.OrderPersistenceEntityBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;

public class OrderPersistenceEntityTestDataBuilder {

    private OrderPersistenceEntityTestDataBuilder() {
    }

    public static OrderPersistenceEntityBuilder existingOrder() {
        return OrderPersistenceEntity.builder()
                .id(IdGenerator.generateTSID().toLong())
                .customer(CustomerPersistenceEntityTestDataBuilder.aCustomer().build())
                .totalItems(3)
                .totalAmount(new BigDecimal(1250))
                .status("DRAFT")
                .paymentMethod("CREDIT_CARD")
                .placedAt(OffsetDateTime.now())
                .billing(BillingEmbeddable.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .document("25508578")
                        .phone("478-256-2604")
                        .email("johndoe@email.com")
                        .address(AddressEmbeddable.builder()
                                .street("Bourbon Street")
                                .number("1134")
                                .complement("Apt. 114")
                                .neighborhood("New Orleans")
                                .city("Louisiana")
                                .state("Louisiana")
                                .zipCode("70130")
                                .build())
                        .build())
                .shipping(ShippingEmbeddable.builder()
                        .cost(new BigDecimal("25.00"))
                        .expectedDate(LocalDate.of(2025, 9, 30))
                        .recipient(RecipientEmbeddable.builder()
                                .firstName("John")
                                .lastName("Doe")
                                .document("25508578")
                                .phone("478-256-2604")
                                .build())
                        .address(AddressEmbeddable.builder()
                                .street("Bourbon Street")
                                .number("1134")
                                .complement("Apt. 114")
                                .neighborhood("North Ville")
                                .city("North Ville")
                                .state("New Orleans")
                                .zipCode("70130")
                                .build())
                        .build())
                .items(Set.of(
                        existingItem().build(),
                        existingItemAlt().build()
                ));
    }

    public static OrderItemPersistenceEntity.OrderItemPersistenceEntityBuilder existingItem() {
        return OrderItemPersistenceEntity.builder()
                .id(IdGenerator.generateTSID().toLong())
                .price(new BigDecimal(500))
                .quantity(2)
                .totalAmount(new BigDecimal(1000))
                .productName("Notebook")
                .productId(IdGenerator.generateTimeBasedUUID());
    }

    public static OrderItemPersistenceEntity.OrderItemPersistenceEntityBuilder existingItemAlt() {
        return OrderItemPersistenceEntity.builder()
                .id(IdGenerator.generateTSID().toLong())
                .price(new BigDecimal(250))
                .quantity(1)
                .totalAmount(new BigDecimal(250))
                .productName("Mouse pad")
                .productId(IdGenerator.generateTimeBasedUUID());
    }
}
