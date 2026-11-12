package com.algaworks.algashop.ecommerce.application.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties("algashop")
public class EcommerceProperties {
	@NotBlank
	private String apiUrl;
	@NotBlank
	private String authWithAlgaSecurityPath;
	@NotBlank
	private String paymentProviderCreditCardTokenUrl;
	@NotBlank
	private String paymentProviderPublicKey;
	@NotBlank
	private String authorizationServerUrl;
	private Features features = new Features();

	@Getter
	@Setter
	public static class Features {
		private boolean homeBffEnabled = false;
	}
}
