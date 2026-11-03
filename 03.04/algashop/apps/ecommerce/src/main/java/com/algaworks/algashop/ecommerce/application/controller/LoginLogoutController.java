package com.algaworks.algashop.ecommerce.application.controller;

import com.algaworks.algashop.ecommerce.application.properties.EcommerceProperties;
import com.algaworks.algashop.ecommerce.infraestructure.security.AlgaShopSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class LoginLogoutController {

	private final AlgaShopSecurityService algaShopSecurityService;
	private final EcommerceProperties ecommerceProperties;

	@GetMapping("/logout")
	public String logout() {
		if (algaShopSecurityService.getAuthentication().isPresent()) {
			return "logout";
		} else {
			return "redirect:/";
		}
	}

	@GetMapping("/logged-out")
	public String loggedOut() {
		return "redirect:/";
	}

	@GetMapping("/login")
	public String login() {
		if (algaShopSecurityService.getAuthentication().isEmpty()) {
			return "redirect:" + ecommerceProperties.getAuthWithAlgaSecurityPath();
		} else {
			return "redirect:/";
		}
	}
}
