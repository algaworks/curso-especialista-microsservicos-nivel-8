package com.algaworks.algashop.ordering.core.application.order.saga;

import com.algaworks.algashop.ordering.core.application.invoice.command.CancelInvoiceIntegrationCommand;
import com.algaworks.algashop.ordering.core.application.invoice.command.IssueInvoiceIntegrationCommand;
import com.algaworks.algashop.ordering.core.application.order.snapshot.OrderSnapshot;
import com.algaworks.algashop.ordering.core.application.order.snapshot.OrderSnapshotAssembler;
import com.algaworks.algashop.ordering.core.application.stock.command.ReserveStockIntegrationCommand;
import com.algaworks.algashop.ordering.core.domain.model.order.Order;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.order.Orders;
import com.algaworks.algashop.ordering.core.ports.in.order.saga.ForCoordinatingPlaceOrderSaga;
import com.algaworks.algashop.ordering.core.ports.out.order.saga.ForPublishingPlaceOrderSagaIntegrationCommands;
import com.algaworks.algashop.ordering.core.ports.out.order.saga.ForStoringPlaceOrderSagas;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PlaceOrderSagaCoordinator implements ForCoordinatingPlaceOrderSaga {

	private final ForStoringPlaceOrderSagas sagas;

	private final Orders orders;
	private final OrderSnapshotAssembler orderSnapshotAssembler;

	private final ForPublishingPlaceOrderSagaIntegrationCommands commands;

	@Override
	public void start(String rawOrderId) {
		OrderId orderId = new OrderId(rawOrderId);
		Order order = findOrder(orderId);
		OrderSnapshot snapshot = orderSnapshotAssembler.toSnapshot(order);
		IssueInvoiceIntegrationCommand command = IssueInvoiceIntegrationCommand.from(snapshot);

		PlaceOrderSaga saga = PlaceOrderSaga.start(orderId);
		saga.moveToInvoicing();
		sagas.add(saga);

		commands.send(saga.sagaId().toString(), command);

		log.info("Place order saga started: saga={} order={}", saga.sagaId(), saga.orderId());
	}

	@Override
	public void onInvoicePaid(UUID sagaId) {
		PlaceOrderSaga saga = findSaga(sagaId);

		if (saga.isReservingStock() || saga.hasSucceeded()) {
			logIgnored(saga);
			return;
		}

		Order order = findOrder(saga.orderId());
		order.markAsPaid();
		orders.add(order);

		saga.moveToReservingStock();
		sagas.add(saga);

		commands.send(saga.sagaId().toString(), reserveStockIntegrationCommand(order));
	}

	@Override
	public void onStockReservationConfirmed(UUID sagaId) {
		PlaceOrderSaga saga = findSaga(sagaId);

		if (saga.hasSucceeded()) {
			logIgnored(saga);
			return;
		}

		Order order = findOrder(saga.orderId());
		order.markAsReady();
		orders.add(order);

		saga.moveToApprovingOrder();
		saga.endSucceeded();
		sagas.add(saga);

	}

	@Override
	public void onInvoiceCanceled(UUID sagaId) {
		PlaceOrderSaga saga = findSaga(sagaId);

		if (saga.isCompensated()) {
			logIgnored(saga);
			return;
		}

		if (!saga.hasFailed()) {
			saga.failWithPaymentRefused();
		}

		Order order = findOrder(saga.orderId());
		order.cancel();
		orders.add(order);

		saga.compensateOrder();
		saga.endCompensated();
		sagas.add(saga);
	}

	@Override
	public void onStockReservationRejected(UUID sagaId) {
		PlaceOrderSaga saga = findSaga(sagaId);
		findOrder(saga.orderId());

		if (saga.wasStockRejected()) {
			logIgnored(saga);
			return;
		}

		saga.failWithOutOfStock();
		saga.compensateInvoice();
		sagas.add(saga);

		commands.send(saga.sagaId().toString(), new CancelInvoiceIntegrationCommand(saga.orderId().toString()));
	}

	private void logIgnored(PlaceOrderSaga saga) {
		log.info("Saga reply was ignored: saga={} order={} status={} step{}",
				saga.sagaId(), saga.orderId(), saga.status(), saga.step());
	}

	private ReserveStockIntegrationCommand reserveStockIntegrationCommand(Order order) {
		return new ReserveStockIntegrationCommand(
				order.id().toString(),
				order.items().stream().map(item -> new ReserveStockIntegrationCommand.Item(
						item.productId().value(), item.quantity().value()
				)).toList()
		);
	}

	private PlaceOrderSaga findSaga(UUID sagaId) {
		return sagas.ofId(sagaId).orElseThrow(()->new PlaceOrderSagaNotFoundException(sagaId));
	}

	private Order findOrder(OrderId orderId) {
		return orders.ofId(orderId).orElseThrow(OrderNotFoundException::new);
	}
}
