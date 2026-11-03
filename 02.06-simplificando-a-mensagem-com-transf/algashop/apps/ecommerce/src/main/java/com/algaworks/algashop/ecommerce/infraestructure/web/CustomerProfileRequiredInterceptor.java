package com.algaworks.algashop.ecommerce.infraestructure.web;

import com.algaworks.algashop.ecommerce.application.client.CustomerRestClient;
import com.algaworks.algashop.ecommerce.infraestructure.security.AlgaShopSecurityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerProfileRequiredInterceptor implements HandlerInterceptor {

	static final String CUSTOMER_PROFILE_PATH = "/my-account/complete-your-profile";
	static final String MY_ACCOUNT_PATH = "/my-account";

	private final AlgaShopSecurityService algaShopSecurityService;
	private final CustomerRestClient customerRestClient;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (!algaShopSecurityService.isAuthenticated()) {
			return true;
		}

		String requestPath = requestPath(request);
		boolean customerProfilePath = isCustomerProfilePath(requestPath);

		try {
			customerRestClient.getMyProfile();
			if (customerProfilePath) {
				response.sendRedirect(request.getContextPath() + MY_ACCOUNT_PATH);
				return false;
			}
			return true;
		} catch (HttpClientErrorException.NotFound e) {
			if (customerProfilePath) {
				return true;
			}
			response.sendRedirect(request.getContextPath() + CUSTOMER_PROFILE_PATH);
			return false;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return true;
		}
	}

	private String requestPath(HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		String contextPath = request.getContextPath();
		if (contextPath != null && !contextPath.isBlank() && requestUri.startsWith(contextPath)) {
			return requestUri.substring(contextPath.length());
		}
		return requestUri;
	}

	private boolean isCustomerProfilePath(String requestPath) {
		return requestPath.equals(CUSTOMER_PROFILE_PATH) || requestPath.startsWith(CUSTOMER_PROFILE_PATH + "/");
	}
}
