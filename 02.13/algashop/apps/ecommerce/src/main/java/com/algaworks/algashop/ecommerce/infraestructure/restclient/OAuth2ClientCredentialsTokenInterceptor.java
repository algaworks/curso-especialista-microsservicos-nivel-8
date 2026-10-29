package com.algaworks.algashop.ecommerce.infraestructure.restclient;

import com.algaworks.algashop.ecommerce.infraestructure.oauth2.OAuth2ClientCredentialsManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2ClientCredentialsTokenInterceptor implements ClientHttpRequestInterceptor {
	private static final String clientRegistrationId = "backend";

	private final OAuth2ClientCredentialsManagerService auth2ClientCredentialsManagerService;

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body,
									   ClientHttpRequestExecution execution) throws IOException {
		OAuth2AccessToken accessToken = auth2ClientCredentialsManagerService.getAccessToken(clientRegistrationId);
		request.getHeaders().setBearerAuth(accessToken.getTokenValue());
		return execution.execute(request, body);
	}
}