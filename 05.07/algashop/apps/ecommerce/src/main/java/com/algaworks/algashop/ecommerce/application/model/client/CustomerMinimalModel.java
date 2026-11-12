package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.Data;

@Data
public class CustomerMinimalModel {
	private String id;
	private String firstName;
	private String lastName;
	private String document;
	private String email;
	private String phone;
}
