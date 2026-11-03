package com.algaworks.algashop.ecommerce.infraestructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class EcommerceWebSecurityConfig {

	@Bean
	@SuppressWarnings("java:S112")
	public SecurityFilterChain filterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
		http
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/checkout/**", "/buy-now/**").authenticated()
				.requestMatchers("/shopping-cart/add/**").authenticated()
				.requestMatchers("/my-account/orders/**").authenticated()
				.requestMatchers("/my-account/details/**").authenticated()
				.requestMatchers("/my-account/address", "/my-account/address/**").authenticated()
				.requestMatchers("/my-account/credit-cards", "/my-account/credit-cards/**").authenticated()
				.requestMatchers("/my-account/complete-your-profile", "/my-account/complete-your-profile/**").authenticated()
				.anyRequest().permitAll()
			)
			.oauth2Login(o -> o.userInfoEndpoint(withDefaults())
				.loginPage("/my-account")
			).logout(logout -> logout
						.logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository))
						.invalidateHttpSession(true)
						.clearAuthentication(true)
						.deleteCookies("JSESSIONID")
				);
		return http.build();
	}

	private LogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
		OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
				new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);

		oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}?logout-success");
		return oidcLogoutSuccessHandler;
	}

	@Bean
	public OAuth2AuthorizedClientRepository authorizedClientRepository() {
		return new HttpSessionOAuth2AuthorizedClientRepository();
	}
}
