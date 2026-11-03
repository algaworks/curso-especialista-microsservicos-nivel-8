package com.algaworks.algashop.ordering.core.application.order.event;

import com.algaworks.algashop.ordering.core.application.notification.command.SendEmailIntegrationCommand;
import com.algaworks.algashop.ordering.core.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.core.domain.model.order.Order;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderItem;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Component
public class OrderPlacedEmailCommandAssembler {

	public SendEmailIntegrationCommand toCommand(Order order, Customer customer, UUID templateId) {
		Objects.requireNonNull(order);
		Objects.requireNonNull(customer);
		Objects.requireNonNull(templateId);

		Map<String, Object> variables = new LinkedHashMap<>();
		variables.put("orderId", order.id().toString());
		variables.put("placedAt", order.placedAt().toString());
		variables.put("totalAmount", order.totalAmount().value());
		variables.put("shippingCost", order.shipping().cost().value());
		variables.put("expectedDeliveryDate", order.shipping().expectedDate().toString());
		variables.put("items", order.items().stream()
				.sorted(Comparator.comparing(item -> item.id().toString()))
				.map(this::toItemVariables)
				.toList());

		return SendEmailIntegrationCommand.builder()
				.aggregateId(order.id().toString())
				.recipient(SendEmailIntegrationCommand.Recipient.builder()
						.email(customer.email().value())
						.name(customer.fullName().toString())
						.build())
				.templateId(templateId)
				.variables(variables)
				.build();
	}

	private Map<String, Object> toItemVariables(OrderItem item) {
		Map<String, Object> variables = new LinkedHashMap<>();
		variables.put("productName", item.productName().value());
		variables.put("quantity", item.quantity().value());
		variables.put("price", item.price().value());
		variables.put("totalAmount", item.totalAmount().value());
		return variables;
	}
}
