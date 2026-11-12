package com.algaworks.algashop.ordering.core.application.order.saga;

import com.algaworks.algashop.ordering.core.application.saga.SagaStatus;
import com.algaworks.algashop.ordering.core.application.saga.SagaTransitionException;
import com.algaworks.algashop.ordering.core.domain.model.IdGenerator;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderId;

import java.util.Objects;
import java.util.UUID;

public class PlaceOrderSaga {

	private final UUID sagaId;
	private final OrderId orderId;
	private SagaStatus status;
	private PlaceOrderSagaStep step;
	private PlaceOrderSagaFailure failure;
	private long version;

	private PlaceOrderSaga(UUID sagaId, OrderId orderId,
	                       SagaStatus status, PlaceOrderSagaStep step,
	                       PlaceOrderSagaFailure failure,
	                       long version) {
		this.sagaId = Objects.requireNonNull(sagaId);
		this.orderId = Objects.requireNonNull(orderId);
		this.status = Objects.requireNonNull(status);
		this.step = Objects.requireNonNull(step);
		this.failure = failure;
		this.version = version;
	}

	public static PlaceOrderSaga start(OrderId orderId) {
		return new PlaceOrderSaga(
				IdGenerator.generateTimeBasedUUID(),
				orderId,
				SagaStatus.RUNNING,
				PlaceOrderSagaStep.PLACING_ORDER,
				null,
				0L
		);
	}

	public static PlaceOrderSaga existing(UUID sagaId, OrderId orderId,
	                                      SagaStatus status,
	                                      PlaceOrderSagaStep step,
	                                      PlaceOrderSagaFailure failure,
	                                      long version) {
		return new PlaceOrderSaga(sagaId, orderId, status, step, failure, version);
	}

	public void moveToInvoicing() {
		require(isRunningAt(PlaceOrderSagaStep.PLACING_ORDER));
		changeStepTo(PlaceOrderSagaStep.INVOICING);
	}

	public void moveToReservingStock() {
		require(isRunningAt(PlaceOrderSagaStep.INVOICING));
		changeStepTo(PlaceOrderSagaStep.RESERVING_STOCK);
	}

	public void moveToApprovingOrder() {
		require(isRunningAt(PlaceOrderSagaStep.RESERVING_STOCK));
		changeStepTo(PlaceOrderSagaStep.APPROVING_ORDER);
	}

	public boolean hasSucceeded() {
		return this.status == SagaStatus.SUCCEEDED;
	}

	public void endSucceeded() {
		require(canEndSucceeded());
		changeStatusTo(SagaStatus.SUCCEEDED);
	}

	public boolean isCompensated() {
		return this.status == SagaStatus.COMPENSATED;
	}

	public boolean hasFailed() {
		return this.failure != null;
	}

	public void failWithPaymentRefused() {
		require(isRunningAt(PlaceOrderSagaStep.INVOICING));
		startCompensation(PlaceOrderSagaFailure.PAYMENT_REFUSED);
	}

	public void failWithOutOfStock() {
		require(isRunningAt(PlaceOrderSagaStep.RESERVING_STOCK));
		startCompensation(PlaceOrderSagaFailure.OUT_OF_STOCK);
	}

	public void compensateOrder(){
		require(canCompensateOrder());
		changeStepTo(PlaceOrderSagaStep.CANCELLING_ORDER);
	}

	public void compensateInvoice(){
		require(canCompensateInvoicing());
		changeStepTo(PlaceOrderSagaStep.CANCELLING_INVOICE);
	}

	public void endCompensated() {
		require(canEndCompensated());
		changeStatusTo(SagaStatus.COMPENSATED);
	}

	public boolean wasStockRejected() {
		return this.failure == PlaceOrderSagaFailure.OUT_OF_STOCK;
	}

	private boolean canEndCompensated() {
		return isCompensatingAt(PlaceOrderSagaStep.CANCELLING_ORDER);
	}

	private boolean canCompensateOrder() {
		return this.status == SagaStatus.COMPENSATING
				&& this.step.canChangeTo(PlaceOrderSagaStep.CANCELLING_ORDER);
	}

	private boolean canCompensateInvoicing() {
		return this.status == SagaStatus.COMPENSATING
				&& this.step.canChangeTo(PlaceOrderSagaStep.CANCELLING_INVOICE);
	}

	private void startCompensation(PlaceOrderSagaFailure failure) {
		changeStatusTo(SagaStatus.COMPENSATING);
		this.failure = failure;
	}

	private boolean canEndSucceeded() {
		return isRunningAt(PlaceOrderSagaStep.APPROVING_ORDER);
	}

	public boolean isReservingStock() {
		return isRunningAt(PlaceOrderSagaStep.RESERVING_STOCK);
	}

	public boolean isRunningAt(PlaceOrderSagaStep step) {
		return this.status == SagaStatus.RUNNING && this.step == step;
	}

	public boolean isCompensatingAt(PlaceOrderSagaStep step) {
		return this.status == SagaStatus.COMPENSATING && this.step == step;
	}

	public boolean isActive() {
		return this.status == SagaStatus.RUNNING || this.status == SagaStatus.COMPENSATING;
	}

	public long version() {
		return version;
	}

	public PlaceOrderSagaStep step() {
		return step;
	}

	public SagaStatus status() {
		return status;
	}

	public OrderId orderId() {
		return orderId;
	}

	public UUID sagaId() {
		return sagaId;
	}

	public PlaceOrderSagaFailure failure() {
		return failure;
	}

	private void require(boolean precondition) {
		if(!precondition) {
			throw SagaTransitionException.notAllowed(orderId(), status(), step());
		}
	}

	private void changeStatusTo(SagaStatus newStatus) {
		if (this.status().canNotChangeTo(newStatus)) {
			throw SagaTransitionException.statusChange(orderId(), status(), newStatus);
		}
		this.status = newStatus;
	}

	private void changeStepTo(PlaceOrderSagaStep newStep) {
		if (step().canNotChangeTo(newStep)) {
			throw SagaTransitionException.stepChange(orderId(), step(), newStep);
		}
		this.step = newStep;
	}

	@Override
	public String toString() {
		return "PlaceOrderSaga{" +
				"sagaId=" + sagaId +
				", orderId=" + orderId +
				", status=" + status +
				", step=" + step +
				", version=" + version +
				'}';
	}

}
