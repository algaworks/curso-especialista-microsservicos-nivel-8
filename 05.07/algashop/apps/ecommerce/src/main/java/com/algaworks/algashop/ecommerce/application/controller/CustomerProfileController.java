package com.algaworks.algashop.ecommerce.application.controller;

import com.algaworks.algashop.ecommerce.application.client.CustomerRestClient;
import com.algaworks.algashop.ecommerce.application.model.client.CustomerInput;
import com.algaworks.algashop.ecommerce.application.model.form.NewCustomerProfileForm;
import com.algaworks.algashop.ecommerce.application.model.page.AlertMessage;
import com.algaworks.algashop.ecommerce.application.util.FullNameParser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CustomerProfileController {

	private static final String EMAIL_ATTR = "email";

	private final CustomerRestClient customerRestClient;

	@GetMapping("/my-account/complete-your-profile")
	public ModelAndView newCustomerProfile(@AuthenticationPrincipal OAuth2User userDetails) {
		try {
			customerRestClient.getMyProfile();
			return new ModelAndView("redirect:/my-account");
		} catch (HttpClientErrorException.NotFound _) {
			return newCustomerProfileView(newCustomerProfileForm(userDetails), userDetails, null);
		} catch (Exception _) {
			log.error("Error loading customer profile", new Exception());
			return newCustomerProfileView(newCustomerProfileForm(userDetails), userDetails, AlertMessage.danger(
					"An unknown error occurred while trying to load your profile. Please try again later."));
		}
	}

	@PostMapping("/my-account/complete-your-profile")
	public ModelAndView createCustomerProfile(
			@Valid @ModelAttribute("newCustomerProfileForm") NewCustomerProfileForm newCustomerProfileForm,
			BindingResult bindingResult,
			@AuthenticationPrincipal OAuth2User userDetails) {
		try {
			customerRestClient.getMyProfile();
			return new ModelAndView("redirect:/my-account");
		} catch (HttpClientErrorException.NotFound _) {
			// The profile is missing, so this request is allowed to create it.
		} catch (Exception _) {
			log.error("Error loading customer profile", new Exception());
			return newCustomerProfileView(newCustomerProfileForm, userDetails, AlertMessage.danger(
					"An unknown error occurred while trying to load your profile. Please try again later."));
		}

		if (bindingResult.hasErrors()) {
			return newCustomerProfileView(newCustomerProfileForm, userDetails, AlertMessage.danger("There are errors in the form!"));
		}

		FullNameParser.NameParts nameParts = FullNameParser.split(newCustomerProfileForm.getFullName());
		String email = userDetails.getAttribute(EMAIL_ATTR);
		if (email == null || email.isBlank()) {
			throw new AccessDeniedException("Authenticated user e-mail not found.");
		}

		try {
			CustomerInput input = CustomerInput.builder()
					.firstName(nameParts.firstName())
					.lastName(nameParts.lastName())
					.email(email)
					.phone(newCustomerProfileForm.getPhone())
					.document(newCustomerProfileForm.getDocument())
					.birthDate(newCustomerProfileForm.getBirthDate())
					.promotionNotificationsAllowed(newCustomerProfileForm.isAllowPromotionNotifications())
					.address(newCustomerProfileForm.getAddress())
					.build();
			customerRestClient.createMyProfile(input);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return newCustomerProfileView(newCustomerProfileForm, userDetails, AlertMessage.danger(
					"An unknown error occurred while trying to create your profile. Please try again later."));
		}

		return new ModelAndView("redirect:/");
	}

	private ModelAndView newCustomerProfileView(NewCustomerProfileForm newCustomerProfileForm,
												OAuth2User userDetails,
												AlertMessage alertMessage) {
		ModelAndView modelAndView = new ModelAndView("myaccount-new-customer");
		modelAndView.addObject("newCustomerProfileForm", newCustomerProfileForm);
		modelAndView.addObject("email", userDetails == null ? null : userDetails.getAttribute("email"));
		modelAndView.addObject("alertMessage", alertMessage);
		return modelAndView;
	}

	private NewCustomerProfileForm newCustomerProfileForm(OAuth2User userDetails) {
		return NewCustomerProfileForm.builder()
				.fullName(userDetails == null ? null : userDetails.getAttribute("name"))
				.build();
	}
}
