package com.algaworks.algashop.ordering.core.application.saga;

import java.util.Arrays;
import java.util.List;

public enum SagaStatus {
	RUNNING,
	COMPENSATING(RUNNING),
	SUCCEEDED(RUNNING),
	COMPENSATED(COMPENSATING);

	SagaStatus(SagaStatus... previousStatuses) {
		this.previousStatuses = Arrays.asList(previousStatuses);
	}

	private final List<SagaStatus> previousStatuses;

	public boolean canChangeTo(SagaStatus newStatus) {
		SagaStatus currentStatus = this;
		return newStatus.previousStatuses.contains(currentStatus);
	}

	public boolean canNotChangeTo(SagaStatus newStatus) {
		return !canChangeTo(newStatus);
	}
}
