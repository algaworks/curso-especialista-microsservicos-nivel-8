package com.algaworks.algashop.ecommerce.infraestructure.oauth2;

public class OAuth2UserAuthorizationRequiredException extends RuntimeException {

	public OAuth2UserAuthorizationRequiredException(String message) {
		super(message);
	}

	public OAuth2UserAuthorizationRequiredException(String message, Throwable cause) {
		super(message, cause);
	}

}
