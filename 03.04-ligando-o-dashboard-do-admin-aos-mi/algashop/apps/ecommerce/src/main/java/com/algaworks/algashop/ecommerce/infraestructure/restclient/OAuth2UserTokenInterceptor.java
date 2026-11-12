package com.algaworks.algashop.ecommerce.infraestructure.restclient;

import com.algaworks.algashop.ecommerce.infraestructure.oauth2.OAuth2UserAuthorizationRequiredException;
import com.algaworks.algashop.ecommerce.infraestructure.security.AlgaShopSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2UserTokenInterceptor implements ClientHttpRequestInterceptor {

    private final AlgaShopSecurityService algaShopSecurityService;
    private final OAuth2AuthorizedClientManager authorizedClientManager;

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body,
				ClientHttpRequestExecution execution) throws IOException {
		var possibleAuthentication = algaShopSecurityService.getAuthentication();

		if (possibleAuthentication.isEmpty()) {
			throw new OAuth2UserAuthorizationRequiredException("OAuth2 user authorization is required.");
		}

        var authentication = possibleAuthentication.get();

        try {
            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                    .withClientRegistrationId(authentication.getAuthorizedClientRegistrationId())
                    .principal(authentication)
                    .build();
            OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

            if (authorizedClient == null || authorizedClient.getAccessToken() == null) {
                throw new AccessDeniedException("OAuth2 user authorization failed.");
            }

            OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
            request.getHeaders().setBearerAuth(accessToken.getTokenValue());
        } catch (OAuth2AuthorizationException | AccessDeniedException e) {
            throw new OAuth2UserAuthorizationRequiredException("OAuth2 user authorization is required.", e);
        }

        return execution.execute(request, body);
    }
}
