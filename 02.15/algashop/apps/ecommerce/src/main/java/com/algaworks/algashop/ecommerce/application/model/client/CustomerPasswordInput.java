package com.algaworks.algashop.ecommerce.application.model.client;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerPasswordInput {

    @NotBlank
    private final String newPassword;

    @NotBlank
    private final String oldPassword;

}
