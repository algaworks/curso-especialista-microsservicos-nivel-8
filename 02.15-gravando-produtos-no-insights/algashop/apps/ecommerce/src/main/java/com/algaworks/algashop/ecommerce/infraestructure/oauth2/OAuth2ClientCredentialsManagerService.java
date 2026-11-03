package com.algaworks.algashop.ecommerce.infraestructure.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OAuth2ClientCredentialsManagerService {

	private final ClientRegistrationRepository clientRegistrationRepository;
	private final OAuth2AuthorizedClientService auth2AuthorizedClientService;
	private final OAuth2AuthorizedClientManager authorizedClientManager;

	public OAuth2AccessToken getAccessToken(String clientRegistrationId) {
		return this.generateToken(clientRegistrationId, clientRegistrationId);
	}

	public OAuth2AccessToken getAccessToken(String clientRegistrationId, Authentication principal) {
		return this.generateToken(clientRegistrationId, principal);
	}

	private OAuth2AccessToken generateToken(String clientRegistrationId, Object principal) {
		Objects.requireNonNull(clientRegistrationId);
		ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId);

		if (clientRegistration == null) {
			throw new AccessDeniedException(String.format("OAuth2 Client %s was not found", clientRegistrationId));
		}

		OAuth2AuthorizedClient oauth2Client = auth2AuthorizedClientService.loadAuthorizedClient(
				clientRegistrationId, clientRegistrationId);

		if (hasAccessToken(oauth2Client)) {
			if (isTokenExpired(oauth2Client)) {
				return generateNewAccessToken(clientRegistrationId, principal);
			}
			return oauth2Client.getAccessToken();
		}

		return generateNewAccessToken(clientRegistrationId, principal);
	}

	private boolean hasAccessToken(OAuth2AuthorizedClient oAuth2AuthorizedClient) {
		return oAuth2AuthorizedClient != null
				&& oAuth2AuthorizedClient.getAccessToken() != null;
	}

	private boolean isTokenExpired(OAuth2AuthorizedClient oAuth2AuthorizedClient) {
		if (oAuth2AuthorizedClient.getAccessToken().getExpiresAt() == null) {
			return false;
		}
		return Instant.now()
				.isAfter(oAuth2AuthorizedClient.getAccessToken().getExpiresAt());
	}

	private OAuth2AccessToken generateNewAccessToken(String clientId, Object principal) {
		OAuth2AuthorizeRequest.Builder request = OAuth2AuthorizeRequest
				.withClientRegistrationId(clientId);

		if (principal instanceof String s) {
			request.principal(s);
		}

		if (principal instanceof Authentication auth) {
			request.principal(auth);
		}

		OAuth2AuthorizedClient auth2AuthorizedClient = authorizedClientManager.authorize(request.build());

		if (auth2AuthorizedClient == null) {
			throw new AccessDeniedException("Authentication failed.");
		}

		return auth2AuthorizedClient.getAccessToken();
	}

}
