package com.algaworks.algashop.ecommerce.application.controller;

import com.algaworks.algashop.ecommerce.application.client.CustomerRestClient;
import com.algaworks.algashop.ecommerce.application.model.client.AddressModel;
import com.algaworks.algashop.ecommerce.application.model.client.CustomerInput;
import com.algaworks.algashop.ecommerce.application.model.client.CustomerModel;
import com.algaworks.algashop.ecommerce.application.model.form.NewCustomerProfileForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerProfileControllerTest {

	@Mock
	private CustomerRestClient customerRestClient;

	@Test
	void shouldFillInitialFormNameWithAuthenticatedUserName() {
		CustomerProfileController controller = new CustomerProfileController(customerRestClient);

		when(customerRestClient.getMyProfile()).thenThrow(notFound());

		ModelAndView modelAndView = controller.newCustomerProfile(oauth2User());

		assertThat(modelAndView.getViewName()).isEqualTo("myaccount-new-customer");
		NewCustomerProfileForm form = (NewCustomerProfileForm) modelAndView.getModel().get("newCustomerProfileForm");
		assertThat(form.getFullName()).isEqualTo("Alex Silva");
		assertThat(modelAndView.getModel().get("email")).isEqualTo("alex@example.com");
	}

	@Test
	void shouldCreateCustomerProfileWithAuthenticatedUserEmailAndRedirectToHome() {
		CustomerProfileController controller = new CustomerProfileController(customerRestClient);
		NewCustomerProfileForm form = newCustomerProfileForm();
		BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(form, "newCustomerProfileForm");
		OAuth2User user = oauth2User();
		ArgumentCaptor<CustomerInput> inputCaptor = ArgumentCaptor.forClass(CustomerInput.class);

		when(customerRestClient.getMyProfile()).thenThrow(notFound());
		when(customerRestClient.createMyProfile(any(CustomerInput.class))).thenReturn(new CustomerModel());

		ModelAndView modelAndView = controller.createCustomerProfile(form, bindingResult, user);

		assertThat(modelAndView.getViewName()).isEqualTo("redirect:/");
		verify(customerRestClient).createMyProfile(inputCaptor.capture());
		CustomerInput input = inputCaptor.getValue();
		assertThat(input.getEmail()).isEqualTo("alex@example.com");
		assertThat(input.getFirstName()).isEqualTo("Alex");
		assertThat(input.getLastName()).isEqualTo("Silva");
		assertThat(input.getAddress().getZipCode()).isEqualTo("12345");
	}

	private NewCustomerProfileForm newCustomerProfileForm() {
		return NewCustomerProfileForm.builder()
				.fullName("Alex Silva")
				.phone("123-456-7890")
				.birthDate(LocalDate.of(1990, 1, 1))
				.document("123-45-678")
				.allowPromotionNotifications(true)
				.address(AddressModel.builder()
						.zipCode("12345")
						.street("Main Street")
						.number("100")
						.complement("Apt 10")
						.neighborhood("Downtown")
						.city("Austin")
						.state("TX")
						.build())
				.build();
	}

	private OAuth2User oauth2User() {
		return new DefaultOAuth2User(
				List.of(new SimpleGrantedAuthority("ROLE_USER")),
				Map.of("email", "alex@example.com", "name", "Alex Silva"),
				"email"
		);
	}

	private HttpClientErrorException notFound() {
		return HttpClientErrorException.NotFound.create(
				HttpStatus.NOT_FOUND,
				"Not Found",
				HttpHeaders.EMPTY,
				new byte[0],
				StandardCharsets.UTF_8
		);
	}
}
