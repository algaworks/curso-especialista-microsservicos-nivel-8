package com.algaworks.algashop.product.catalog.infrastructure.listener.product;

import com.algaworks.algashop.product.catalog.application.product.event.*;
import com.algaworks.algashop.product.catalog.application.utility.Mapper;
import com.algaworks.algashop.product.catalog.domain.model.product.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductEventListener {

    private final ProductIntegrationEventPublisher integrationEventPublisher;
    private final Mapper mapper;

    @TransactionalEventListener
    @Async
    public void handle(ProductPriceChangedEvent event) {
        log.info("ProductPriceChangedEvent " + event);

        var integrationEvent = mapper.convert(event, ProductPriceChangedIntegrationEvent.class);
        var v2IntegrationEvent = mapper.convert(event, ProductPriceChangedV2IntegrationEvent.class);

        integrationEventPublisher.send(integrationEvent);
        integrationEventPublisher.send(v2IntegrationEvent);
    }

    @TransactionalEventListener
    @Async
    public void handle(ProductPlacedOnSaleEvent event) {
        log.info("ProductPlacedOnSaleEvent " + event);
        var integrationEvent = mapper.convert(event, ProductPlacedOnSaleIntegrationEvent.class);
        integrationEventPublisher.send(integrationEvent);
    }

    @TransactionalEventListener
    @Async
    public void handle(ProductAddedEvent event) {
        log.info("ProductAddedEvent " + event);
        ProductAddedIntegrationEvent integrationEvent = mapper.convert(event, ProductAddedIntegrationEvent.class);
        integrationEventPublisher.send(integrationEvent);
    }

    @TransactionalEventListener
    @Async
    public void handle(ProductDelistedEvent event) {
        log.info("ProductDelistedEvent  " + event);
        var integrationEvent = mapper.convert(event, ProductDelistedIntegrationEvent.class);
        integrationEventPublisher.send(integrationEvent);
    }

    @TransactionalEventListener
    @Async
    public void handle(ProductListedEvent event) {
        log.info("ProductListedEvent " + event);
        var integrationEvent = mapper.convert(event, ProductListedIntegrationEvent.class);
        integrationEventPublisher.send(integrationEvent);
    }

    @TransactionalEventListener
    @Async
    public void handle(ProductRestockedEvent event) {
        log.info("ProductRestockedEvent  " + event);
        var integrationEvent = mapper.convert(event, ProductRestockedIntegrationEvent.class);
        integrationEventPublisher.send(integrationEvent);
    }

    @TransactionalEventListener
    @Async
    public void handle(ProductSoldOutEvent event) {
        log.info("ProductSoldOutEvent " + event);
        var integrationEvent = mapper.convert(event, ProductSoldOutIntegrationEvent.class);
        integrationEventPublisher.send(integrationEvent);
    }

}
