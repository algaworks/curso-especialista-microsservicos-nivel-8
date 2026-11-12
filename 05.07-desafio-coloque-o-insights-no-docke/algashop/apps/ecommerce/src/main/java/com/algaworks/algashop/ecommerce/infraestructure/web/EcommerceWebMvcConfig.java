package com.algaworks.algashop.ecommerce.infraestructure.web;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class EcommerceWebMvcConfig implements WebMvcConfigurer {

	private final CustomerProfileRequiredInterceptor customerProfileRequiredInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(customerProfileRequiredInterceptor)
				.addPathPatterns(
						"/checkout/**",
						"/buy-now/**",
						"/shopping-cart",
						"/shopping-cart/add/**",
						"/my-account",
						"/my-account/**"
				);
	}
}
