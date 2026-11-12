package com.algaworks.algashop.ordering.core.application.order.snapshot;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record PaymentSnapshot(
        @NotBlank String method,
        UUID creditCardId
) {}