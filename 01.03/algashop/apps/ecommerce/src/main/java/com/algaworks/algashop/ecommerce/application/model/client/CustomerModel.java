package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerModel {
	private String id;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private LocalDate birthDate;
	private String document;
	private boolean promotionNotificationsAllowed;
	private AddressModel address;

	public String getFullName() {
		if (firstName == null && lastName == null) {
			return "";
		}
		if (firstName == null) {
			return lastName;
		}
		if (lastName == null) {
			return firstName;
		}
		return firstName + " " + lastName;
	}

	public boolean isAllowPromotionNotifications() {
		return promotionNotificationsAllowed;
	}
}
