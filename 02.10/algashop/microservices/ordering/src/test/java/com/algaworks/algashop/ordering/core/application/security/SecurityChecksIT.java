package com.algaworks.algashop.ordering.core.application.security;

import com.algaworks.algashop.ordering.core.application.AbstractApplicationIT;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.utils.WithMockJwt;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

class SecurityChecksIT extends AbstractApplicationIT {

	@Autowired
	private SecurityChecks securityChecks;

	@Test
	void givenAuthenticatedCustomerShouldAllowOrderForHimself() {
		UUID customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID.value();
		boolean canOrderFor = securityChecks.canOrderFor(customerId);
		Assertions.assertThat(canOrderFor).isTrue();
	}

	@Test
	@WithMockJwt(role = "", audiences = "machine-client-id", subject = "machine-client-id")
	void givenAuthenticatedMachineShouldNotAllowOrder() {
		UUID customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID.value();
		boolean canOrderFor = securityChecks.canOrderFor(customerId);
		Assertions.assertThat(canOrderFor).isFalse();
	}

	@Test
	@WithMockJwt(role = "", audiences = "machine-client-id", subject = "machine-client-id")
	void givenAuthenticatedMachineShouldReturnTrue() {
		boolean machineAuthenticated = securityChecks.isMachineAuthenticated();
		Assertions.assertThat(machineAuthenticated).isTrue();
	}

}