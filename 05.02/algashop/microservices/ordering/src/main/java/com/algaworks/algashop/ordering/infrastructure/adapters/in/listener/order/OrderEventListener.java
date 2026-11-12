package com.algaworks.algashop.ordering.infrastructure.adapters.in.listener.order;

import com.algaworks.algashop.ordering.core.application.order.event.*;
import com.algaworks.algashop.ordering.core.application.utility.Mapper;
import com.algaworks.algashop.ordering.core.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.core.domain.model.order.*;
import com.algaworks.algashop.ordering.core.ports.in.order.saga.ForCoordinatingPlaceOrderSaga;
import com.algaworks.algashop.ordering.core.ports.out.notification.ForPublishingNotificationCommands;
import com.algaworks.algashop.ordering.core.ports.out.order.ForPublishingOrderIntegrationEvents;
import com.algaworks.algashop.ordering.infrastructure.config.NotificationsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final ForPublishingOrderIntegrationEvents forPublishingOrderIntegrationEvents;
    private final Mapper mapper;

    private final OrderPlacedIntegrationEventAssembler orderPlacedIntegrationEventAssembler;
    private final OrderPaidIntegrationEventAssembler orderPaidIntegrationEventAssembler;
    private final Orders orders;

    private final ForCoordinatingPlaceOrderSaga forCoordinatingPlaceOrderSaga;

    private final Customers customers;
    private final OrderPlacedEmailCommandAssembler orderPlacedEmailCommandAssembler;
    private final ForPublishingNotificationCommands forPublishingNotificationCommands;
    private final NotificationsProperties notificationsProperties;

    @EventListener
    public void onOrderPlacedPublishIntegrationEvent(OrderPlacedEvent event) {
        Order order = orders.ofId(event.orderId()).orElseThrow(OrderNotFoundException::new);
        OrderPlacedIntegrationEvent integrationEvent = orderPlacedIntegrationEventAssembler.toIntegrationEvent(order);
        forPublishingOrderIntegrationEvents.send(integrationEvent);
    }

    @EventListener
    public void onOrderPlacedStartSaga(OrderPlacedEvent event) {
        forCoordinatingPlaceOrderSaga.start(event.orderId().toString());
    }

    @EventListener
    public void onOrderPlacedSendConfirmationEmail(OrderPlacedEvent event) {
        Order order = orders.ofId(event.orderId()).orElseThrow(OrderNotFoundException::new);
        Customer customer = customers.ofId(order.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(order.customerId()));
        forPublishingNotificationCommands.send(orderPlacedEmailCommandAssembler.toCommand(
                order, customer, notificationsProperties.getEmailTemplates().getOrderPlaced()));
    }

    @EventListener
    public void listen(OrderPaidEvent event) {
        Order order = orders.ofId(event.orderId()).orElseThrow(OrderNotFoundException::new);
        OrderPaidIntegrationEvent integrationEvent = orderPaidIntegrationEventAssembler.toIntegrationEvent(order);
        forPublishingOrderIntegrationEvents.send(integrationEvent);
    }

    @EventListener
    public void listen(OrderReadyEvent event) {
        OrderReadyIntegrationEvent integrationEvent = mapper.convert(event, OrderReadyIntegrationEvent.class);
        forPublishingOrderIntegrationEvents.send(integrationEvent);
    }

    @EventListener
    public void listen(OrderCanceledEvent event) {
        OrderCanceledIntegrationEvent integrationEvent = mapper.convert(event, OrderCanceledIntegrationEvent.class);
        forPublishingOrderIntegrationEvents.send(integrationEvent);
    }

}
