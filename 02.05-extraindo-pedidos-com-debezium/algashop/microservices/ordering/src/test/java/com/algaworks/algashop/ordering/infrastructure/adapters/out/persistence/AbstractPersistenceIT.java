package com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence;

import com.algaworks.algashop.ordering.core.application.security.SecurityChecks;
import com.algaworks.algashop.ordering.infrastructure.config.auditing.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.utils.CacheTestConfig;
import com.algaworks.algashop.ordering.utils.TestcontainerPostgreSQLConfig;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainerPostgreSQLConfig.class, SpringDataAuditingConfig.class, CacheTestConfig.class})
public abstract class AbstractPersistenceIT {

	@MockitoBean
	protected SecurityChecks securityChecks;

	@BeforeEach
	public void setup() {
		Mockito.when(securityChecks.isAuthenticated()).thenReturn(true);
		Mockito.when(securityChecks.isMachineAuthenticated()).thenReturn(false);
		Mockito.when(securityChecks.getAuthenticatedUserId()).thenReturn(UUID.randomUUID());
	}

}
