package com.algaworks.algashop.product.catalog.infrastructure.message;

import com.algaworks.algashop.product.catalog.domain.model.DomainEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Configuration
public class DomainEventPublisherConfig {

    @Bean
    public DomainEventPublisher domainEventPublisher(
            ApplicationEventPublisher applicationEventPublisher
    ) {
        return event -> {
            if (!TransactionSynchronizationManager.isSynchronizationActive()) {
                applicationEventPublisher.publishEvent(event);
                return;
            }
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void beforeCommit(boolean readOnly) {
                            applicationEventPublisher.publishEvent(event);
                        }
                    }
            );
        };
    }

}
