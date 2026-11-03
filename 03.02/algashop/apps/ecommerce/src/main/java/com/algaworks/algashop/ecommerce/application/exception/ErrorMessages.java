package com.algaworks.algashop.ecommerce.application.exception;

public class ErrorMessages {

	private ErrorMessages() {}

	public static final String END_USER_GENERIC_ERROR_MESSAGE
			= "An unexpected internal error occurred in the system. Please try again later, and if the issue persists, " +
			"contact the system administrator.";

	public static final String END_USER_ORDER_IS_PROCESSING = "Your order is being processed. " +
			"We will provide more details shortly.";

	public static final String END_USER_ORDER_CANT_BE_LOADED = "The data for your order could not be loaded. " +
			"An unexpected internal error occurred in the system. Please try again later, and if the issue persists, " +
			"contact the system administrator.";

	public static final String END_USER_ORDER_PAYMENT_CANT_BE_LOADED = "The payment data for your order could not be loaded. " +
			"An unexpected internal error occurred in the system. Please try again later, and if the issue persists, " +
			"contact the system administrator.";

	public static final String END_USER_ORDER_PAYMENT_IS_PROCESSING = "The payment for your order is being processed. " +
			"We will provide more details shortly.";
}
