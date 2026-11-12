package com.algaworks.algashop.ordering.core.application;

import com.algaworks.algashop.ordering.utils.CacheTestConfig;
import com.algaworks.algashop.ordering.utils.MockJwtDecoderConfig;
import com.algaworks.algashop.ordering.utils.TestcontainerPostgreSQLConfig;
import com.algaworks.algashop.ordering.utils.WithMockJwt;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainerPostgreSQLConfig.class, MockJwtDecoderConfig.class, CacheTestConfig.class})
@WithMockJwt
public abstract class AbstractApplicationIT {

}
