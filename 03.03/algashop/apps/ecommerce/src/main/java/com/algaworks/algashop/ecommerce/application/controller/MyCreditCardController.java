package com.algaworks.algashop.ecommerce.application.controller;

import com.algaworks.algashop.ecommerce.application.client.CreditCardClient;
import com.algaworks.algashop.ecommerce.application.model.client.CreditCardModel;
import com.algaworks.algashop.ecommerce.application.model.client.TokenizedCreditCardInput;
import com.algaworks.algashop.ecommerce.application.properties.EcommerceProperties;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MyCreditCardController {

	private final CreditCardClient creditCardClient;
	private final EcommerceProperties ecommerceProperties;

	@GetMapping("/my-account/credit-cards")
	public ModelAndView creditCards() {
		ModelAndView modelAndView = new ModelAndView("myaccount-credit-cards");
		modelAndView.addObject("creditCards", loadCreditCards());
		modelAndView.addObject("paymentProviderCreditCardTokenUrl", ecommerceProperties.getPaymentProviderCreditCardTokenUrl());
		modelAndView.addObject("paymentProviderPublicKey", ecommerceProperties.getPaymentProviderPublicKey());
		return modelAndView;
	}

	@GetMapping("/my-account/credit-cards/list")
	public ResponseEntity<List<CreditCardModel>> list() {
		return ResponseEntity.ok(loadCreditCards());
	}

	@PostMapping("/my-account/credit-cards")
	public ResponseEntity<CreditCardModel> register(@RequestBody @Valid TokenizedCreditCardInput input,
													BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().build();
		}

		try {
			return ResponseEntity.ok(creditCardClient.register(input));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ResponseEntity.badRequest().build();
		}
	}

	@PostMapping("/my-account/credit-cards/remove/{creditCardId}")
	public ResponseEntity<Void> remove(@PathVariable String creditCardId) {
		try {
			creditCardClient.deleteById(creditCardId);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ResponseEntity.badRequest().build();
		}
	}

	private List<CreditCardModel> loadCreditCards() {
		try {
			List<CreditCardModel> creditCards = creditCardClient.findAll();
			return creditCards == null ? List.of() : creditCards;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return List.of();
		}
	}
}
