package com.algaworks.algashop.ordering.infrastructure.adapters.out.messaging.outbox;

import lombok.Builder;

@Builder
public record OutboxDraft(String channelName, String aggregateId,
                          String correlationId, String replyTopic,
                          Object payload) {
}
