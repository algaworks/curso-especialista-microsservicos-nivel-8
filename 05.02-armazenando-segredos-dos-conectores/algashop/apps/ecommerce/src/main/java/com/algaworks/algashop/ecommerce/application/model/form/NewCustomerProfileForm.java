package com.algaworks.algashop.ecommerce.application.model.form;

import com.algaworks.algashop.ecommerce.application.model.client.AddressModel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCustomerProfileForm {
	@NotBlank
	private String fullName;

	@NotBlank
	private String phone;

	@Past
	@NotNull
	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private LocalDate birthDate;

	@NotBlank
	private String document;

	private boolean allowPromotionNotifications;

	@Valid
	@NotNull
	@Builder.Default
	private AddressModel address = new AddressModel();
}
