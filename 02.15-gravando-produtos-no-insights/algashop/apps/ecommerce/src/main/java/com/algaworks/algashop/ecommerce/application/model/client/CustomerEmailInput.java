package com.algaworks.algashop.ecommerce.application.model.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomerEmailInput {

    @NotBlank
    @Email
    private String email;

}
