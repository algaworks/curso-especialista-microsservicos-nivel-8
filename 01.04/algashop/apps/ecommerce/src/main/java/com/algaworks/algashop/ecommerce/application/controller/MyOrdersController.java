package com.algaworks.algashop.ecommerce.application.controller;

import com.algaworks.algashop.ecommerce.application.client.OrderClient;
import com.algaworks.algashop.ecommerce.application.exception.ErrorMessages;
import com.algaworks.algashop.ecommerce.application.model.client.OrderModel;
import com.algaworks.algashop.ecommerce.application.model.client.OrderModelPage;
import com.algaworks.algashop.ecommerce.application.model.client.OrderStatus;
import com.algaworks.algashop.ecommerce.application.model.client.PageLinkModel;
import com.algaworks.algashop.ecommerce.application.model.client.Paginator;
import com.algaworks.algashop.ecommerce.application.model.filter.OrderFilter;
import com.algaworks.algashop.ecommerce.application.model.page.AlertMessage;
import com.algaworks.algashop.ecommerce.application.model.page.MyOrderDetailPageModel;
import com.algaworks.algashop.ecommerce.application.model.page.MyOrdersPageModel;
import com.algaworks.algashop.ecommerce.application.util.SafeEnumConverterUtil;
import org.apache.commons.lang3.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MyOrdersController {

	private final OrderClient orderClient;

	@GetMapping("/my-account/orders")
	public ModelAndView getOrders(@PageableDefault Pageable pageable) {
		var pageBuilder = MyOrdersPageModel.builder();

		OrderFilter orderFilter = OrderFilter.of(pageable);

		try {
			OrderModelPage orders = orderClient.getOrders(orderFilter);
			List<PageLinkModel> pageLinks = Paginator.calculatePages(orders, "/my-account/orders");
			pageBuilder.ordersPage(orders);
			pageBuilder.pageLinks(pageLinks);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return pageBuilder.build().toModelAndView();
	}

	@GetMapping("/my-account/orders/{orderCode}")
	public ModelAndView getOrderDetail(@PathVariable String orderCode) {
		var pageBuilder = MyOrderDetailPageModel.builder();

		pageBuilder.orderCode(orderCode);

		OrderModel order = null;

		try {
			order = orderClient.getOrder(orderCode);
			pageBuilder.order(order);
		}  catch (HttpClientErrorException.NotFound e) {
			pageBuilder.loadingMessage(ErrorMessages.END_USER_ORDER_IS_PROCESSING);
		} catch (Exception e) {
			pageBuilder.alertMessage(AlertMessage.danger(ErrorMessages.END_USER_ORDER_CANT_BE_LOADED));
		}

		if (order != null) {
			pageBuilder.currentOrderStatus(order.getStatus());
		}

		pageBuilder.autoRefresh(order == null || !isEndStatus(order.getStatus()));

		return pageBuilder.build().toModelAndView();
	}

	private boolean isEndStatus(String rawOrderStatus) {
		if (StringUtils.isBlank(rawOrderStatus)){
			return false;
		}
		OrderStatus orderStatus = SafeEnumConverterUtil.safeParseValue(OrderStatus.class, rawOrderStatus);
		if (orderStatus != null) {
			return switch (orderStatus) {
				case CANCELED, READY, PAID -> true;
				default -> false;
			};
		}
		return false;
	}

	@GetMapping("/my-account/orders/{orderCode}/check")
	public ResponseEntity<Void> checkOrderIsReady(@PathVariable String orderCode, @RequestParam(required = false) String currentOrderStatus) {
		try {
			OrderModel order = orderClient.getOrder(orderCode);
			if (order.getStatus().equalsIgnoreCase(currentOrderStatus)) {
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.ok().build();
		} catch (HttpClientErrorException.NotFound e) { //t.HttpClientErrorException$Unauthorized
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}

}