package com.algaworks.algashop.product.catalog.infrastructure.kafka;

import com.algaworks.algashop.product.catalog.application.stock.command.ReserveStockIntegrationCommand;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;
import java.util.UUID;

class KafkaBeanValidationConfigurerTest {

    private LocalValidatorFactoryBean validator;
    private KafkaListenerEndpointRegistrar registrar;

    @BeforeEach
    void setUp() {
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        registrar = new KafkaListenerEndpointRegistrar();
        new KafkaBeanValidationConfigurer(validator).configureKafkaListeners(registrar);
    }

    @Test
    void givenConfigurer_whenConfigureKafkaListeners_shouldRegisterTheValidator() {
        Assertions.assertThat(registrar.getValidator()).isSameAs(validator);
    }

    @Test
    void givenCommandWithoutItems_whenValidateWithRegisteredValidator_shouldReportViolation() {
        ReserveStockIntegrationCommand command = ReserveStockIntegrationCommand.builder()
                .orderId("01K1Z5T7Q4XK1B9Q2H3J4K5M6N")
                .build();
        Errors errors = new BeanPropertyBindingResult(command, "command");

        registrar.getValidator().validate(command, errors);

        Assertions.assertThat(errors.hasFieldErrors("items")).isTrue();
    }

    @Test
    void givenValidCommand_whenValidateWithRegisteredValidator_shouldNotReportViolation() {
        ReserveStockIntegrationCommand command = ReserveStockIntegrationCommand.builder()
                .orderId("01K1Z5T7Q4XK1B9Q2H3J4K5M6N")
                .items(List.of(new ReserveStockIntegrationCommand.Item(UUID.randomUUID(), 2)))
                .build();
        Errors errors = new BeanPropertyBindingResult(command, "command");

        registrar.getValidator().validate(command, errors);

        Assertions.assertThat(errors.hasErrors()).isFalse();
    }
}
