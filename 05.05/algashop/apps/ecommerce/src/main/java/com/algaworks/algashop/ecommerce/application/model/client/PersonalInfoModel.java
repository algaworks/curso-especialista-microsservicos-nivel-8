package com.algaworks.algashop.ecommerce.application.model.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonalInfoModel {

    @NotBlank
    private String fullName;

    @NotBlank
    private String document;

    @NotBlank
    private String phone;

    @NotNull
    @Valid
    private AddressModel address;

    public String getAddressLine() {
        return address.getAddressLine();
    }

}