package com.algaworks.algashop.ecommerce.infraestructure.restclient;

import org.springframework.boot.restclient.autoconfigure.RestClientBuilderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

	@Bean
	@Primary
	RestClient restClient(RestClientBuilderConfigurer configurer,
				OAuth2ClientCredentialsTokenInterceptor oAuth2ClientCredentialsTokenInterceptor) {
		return configurer.configure(RestClient.builder())
				.requestFactory(generateClientHttpRequestFactory())
				.requestInterceptor(oAuth2ClientCredentialsTokenInterceptor)
				.build();
	}
	
	@Bean
	RestClient userAuthenticatedRestClient(RestClientBuilderConfigurer configurer,
			   OAuth2UserTokenInterceptor oAuth2UserTokenInterceptor) {
		return configurer.configure(RestClient.builder())
				.requestFactory(generateClientHttpRequestFactory())
				.requestInterceptor(oAuth2UserTokenInterceptor)
				.build();
	}

	private ClientHttpRequestFactory generateClientHttpRequestFactory() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setReadTimeout(Duration.ofMinutes(1));
		factory.setConnectTimeout(Duration.ofSeconds(10));
		return factory;
	}
}
