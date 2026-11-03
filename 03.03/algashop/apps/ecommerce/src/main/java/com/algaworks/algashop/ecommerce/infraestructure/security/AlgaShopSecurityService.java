package com.algaworks.algashop.ecommerce.infraestructure.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AlgaShopSecurityService {
	public Optional<OAuth2AuthenticationToken> getAuthentication() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof OAuth2AuthenticationToken token) {
			return Optional.of(token);
		}
		return Optional.empty();
	}

	public String getAuthenticationName() {
		Optional<OAuth2AuthenticationToken> authentication = getAuthentication();
		return authentication.map(oauth2Token -> (String) oauth2Token.getPrincipal().getAttribute("name")).orElse("guest");
	}

	public boolean isAuthenticated() {
		return getAuthentication().isPresent();
	}

	public void forceLogout() {
		SecurityContextHolder.clearContext();
	}
}
