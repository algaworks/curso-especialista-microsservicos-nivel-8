package com.algaworks.algashop.ordering.infrastructure.adapters.in.messaging.kafka.saga;

import com.algaworks.algashop.ordering.core.application.InboundIntegrationReply;
import com.algaworks.algashop.ordering.core.application.invoice.reply.InvoiceCanceledIntegrationReply;
import com.algaworks.algashop.ordering.core.application.invoice.reply.InvoicePaidIntegrationReply;
import com.algaworks.algashop.ordering.core.application.stock.reply.StockReservationConfirmedIntegrationReply;
import com.algaworks.algashop.ordering.core.application.stock.reply.StockReservationRejectedIntegrationReply;
import com.algaworks.algashop.ordering.core.ports.in.order.saga.ForCoordinatingPlaceOrderSaga;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
@KafkaListener(
        id = "#{algaShopMessagingKafkaProperties.sagaRepliesConsumerGroup}",
        concurrency = "3",
        topics = "#{algaShopMessagingKafkaProperties.sagaRepliesTopicName}")
public class KafkaPlaceOrderSagaReplyListener {

    private final ForCoordinatingPlaceOrderSaga coordinator;

    @KafkaHandler
    public void handle(
            @Payload @Valid InvoicePaidIntegrationReply reply,
            @Header(value = KafkaHeaders.CORRELATION_ID, required = false) byte[] correlationId,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
            @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
            @Header(value = KafkaHeaders.OFFSET, required = false) Long offset) {
        UUID sagaId = requireSagaId(correlationId);
        logReceived(reply, sagaId, messageKey, partition, offset);
        coordinator.onInvoicePaid(sagaId);
    }

    @KafkaHandler
    public void handle(
            @Payload @Valid InvoiceCanceledIntegrationReply reply,
            @Header(value = KafkaHeaders.CORRELATION_ID, required = false) byte[] correlationId,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
            @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
            @Header(value = KafkaHeaders.OFFSET, required = false) Long offset) {
        UUID sagaId = requireSagaId(correlationId);
        logReceived(reply, sagaId, messageKey, partition, offset);
        coordinator.onInvoiceCanceled(sagaId);
    }

    @KafkaHandler
    public void handle(
            @Payload @Valid StockReservationConfirmedIntegrationReply reply,
            @Header(value = KafkaHeaders.CORRELATION_ID, required = false) byte[] correlationId,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
            @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
            @Header(value = KafkaHeaders.OFFSET, required = false) Long offset) {
        UUID sagaId = requireSagaId(correlationId);
        logReceived(reply, sagaId, messageKey, partition, offset);
        coordinator.onStockReservationConfirmed(sagaId);
    }

    @KafkaHandler
    public void handle(
            @Payload @Valid StockReservationRejectedIntegrationReply reply,
            @Header(value = KafkaHeaders.CORRELATION_ID, required = false) byte[] correlationId,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
            @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
            @Header(value = KafkaHeaders.OFFSET, required = false) Long offset) {
        UUID sagaId = requireSagaId(correlationId);
        logReceived(reply, sagaId, messageKey, partition, offset);
        coordinator.onStockReservationRejected(sagaId);
    }

    // o tópico é exclusivo da saga: tipo desconhecido é erro de contrato
    @KafkaHandler(isDefault = true)
    public void handle(
            @Payload Object reply,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
            @Header(value = KafkaHeaders.OFFSET, required = false) Long offset) {
        log.warn("Unsupported saga reply: type={} key={} offset={}",
                reply.getClass().getSimpleName(), messageKey, offset);
        throw new IllegalArgumentException(
                "Unsupported saga reply type " + reply.getClass().getSimpleName());
    }

    private UUID requireSagaId(byte[] correlationId) {
        if (correlationId == null || correlationId.length == 0) {
            throw new IllegalArgumentException("Saga reply without the correlation id header");
        }
        String value = new String(correlationId, StandardCharsets.UTF_8);
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Saga reply with an invalid correlation id: " + value, e);
        }
    }

    private void logReceived(InboundIntegrationReply reply, UUID sagaId, String messageKey, Integer partition,
                             Long offset) {
        log.info("Received {} | correlationId={} | key={} | partition={} | offset={} | thread={}",
                reply.getClass().getSimpleName(), sagaId, messageKey, partition, offset,
                Thread.currentThread().getName());
    }
}