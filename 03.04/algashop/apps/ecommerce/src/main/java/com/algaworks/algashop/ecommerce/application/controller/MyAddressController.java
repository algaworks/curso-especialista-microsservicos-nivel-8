package com.algaworks.algashop.ecommerce.application.controller;

import com.algaworks.algashop.ecommerce.application.client.CustomerRestClient;
import com.algaworks.algashop.ecommerce.application.model.client.CustomerModel;
import com.algaworks.algashop.ecommerce.application.model.client.CustomerUpdateInput;
import com.algaworks.algashop.ecommerce.application.model.form.EditAddressForm;
import com.algaworks.algashop.ecommerce.application.model.page.AlertMessage;
import com.algaworks.algashop.ecommerce.application.util.FullNameParser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MyAddressController {

	private final CustomerRestClient customerManagementAPIClient;

	@GetMapping("/my-account/address")
	public ModelAndView getMyAddress(@ModelAttribute("editAddressForm") EditAddressForm editAddressForm,
									 @ModelAttribute("alertMessage") AlertMessage alertMessage) {

		try {
			CustomerModel customer = customerManagementAPIClient.getMyProfile();
			editAddressForm = EditAddressForm.of(customer);
		} catch (HttpClientErrorException.NotFound e) {
			return new ModelAndView("redirect:/my-account/complete-your-profile");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return getMyAddressFreshView(editAddressForm, alertMessage);
	}

	private ModelAndView getMyAddressFreshView(EditAddressForm editAddressForm, AlertMessage alertMessage) {
		ModelAndView modelAndView = new ModelAndView("myaccount-your-address");
		modelAndView.addObject("alertMessage", alertMessage);
		modelAndView.addObject("editAddressForm", editAddressForm);
		return modelAndView;
	}

	@PostMapping("/my-account/address")
	public ModelAndView editMyAddress(@Valid @ModelAttribute("editAddressForm") EditAddressForm editAddressForm,
								   BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return getMyAddressFreshView(editAddressForm, AlertMessage.danger("There are errors in the form!"));
		}

		CustomerModel customer;
		try {
			customer = customerManagementAPIClient.getMyProfile();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return getMyAddressFreshView(editAddressForm, AlertMessage.danger(
					"An unknown error occurred while trying to load your data. Please try again later."));
		}

		CustomerUpdateInput input = CustomerUpdateInput.builder()
				.firstName(FullNameParser.split(customer.getFullName()).firstName())
				.lastName(FullNameParser.split(customer.getFullName()).lastName())
				.phone(customer.getPhone())
				.promotionNotificationsAllowed(customer.isAllowPromotionNotifications())
				.address(editAddressForm.toAddress())
				.build();

		try {
			customerManagementAPIClient.updateMyProfile(input);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return getMyAddressFreshView(editAddressForm, AlertMessage.danger(
					"An unknown error occurred while trying to edit your details. Please try again later."));
		}

		redirectAttributes.addFlashAttribute("alertMessage", AlertMessage.success("Your profile has been edited successfully!"));

		return new ModelAndView("redirect:/my-account/address");
	}


}
