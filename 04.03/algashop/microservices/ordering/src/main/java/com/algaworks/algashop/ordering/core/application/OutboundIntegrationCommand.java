package com.algaworks.algashop.ordering.core.application;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public interface OutboundIntegrationCommand {
	@JsonIgnore
	String getAggregateId();

	@JsonIgnore
	default UUID getIdempotencyKey() {
		return null;
	}
}
