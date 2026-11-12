package com.algaworks.algashop.ordering.infrastructure.adapters.in.web;

import com.algaworks.algashop.ordering.utils.CacheTestConfig;
import com.algaworks.algashop.ordering.utils.MockJwtDecoderConfig;
import com.algaworks.algashop.ordering.utils.MockJwtFactory;
import com.algaworks.algashop.ordering.utils.TestcontainerPostgreSQLConfig;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.specification.RequestSpecification;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.config.JsonConfig.jsonConfig;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:db/testdata/afterMigrate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:db/clean/afterMigrate.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@Import({TestcontainerPostgreSQLConfig.class, MockJwtDecoderConfig.class, CacheTestConfig.class})
public abstract class AbstractPresentationIT {

    @LocalServerPort
    protected int port;

    protected static WireMockServer wireMockProductCatalog;
    protected static WireMockServer wireMockRapidex;

    @MockitoBean("productCatalogAPIClientInterceptor")
    protected OAuth2ClientHttpRequestInterceptor productCatalogAPIClientInterceptor;

    protected void beforeEach() {
        try {
            Mockito.doAnswer(invocation -> {
                HttpRequest request = invocation.getArgument(0);
                byte[] body = invocation.getArgument(1);
                ClientHttpRequestExecution execution = invocation.getArgument(2);
                return execution.execute(request, body); // segue sem token
            }).when(productCatalogAPIClientInterceptor).intercept(any(), any(), any());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
    }

    protected RequestSpecification givenAuthenticated(String tokenValue) {
        return RestAssured.given()
                .header("Authorization",
                        "Bearer " + tokenValue);
    }

    protected RequestSpecification givenAuthenticated() {
        return givenAuthenticated(MockJwtFactory.DEFAULT_TOKEN_VALUE);
    }

    protected RequestSpecification givenAuthenticatedAdmin() {
        return givenAuthenticated(MockJwtFactory.ADMIN_TOKEN_VALUE);
    }

    protected RequestSpecification givenWithExpiredToken() {
        return givenAuthenticated(MockJwtFactory.EXPIRED_TOKEN_VALUE);
    }

    protected RequestSpecification givenAuthenticatedWithNoScopeToken() {
        return givenAuthenticated(MockJwtFactory.NO_SCOPE_TOKEN_VALUE);
    }

    protected RequestSpecification givenAuthenticatedUnknownCustomer() {
        return givenAuthenticated(MockJwtFactory.UNKNOWN_CUSTOMER_TOKEN_VALUE);
    }

    protected static void initWireMock() {
        wireMockRapidex = new WireMockServer(options()
                .templatingEnabled(true)
                .port(8780)
                .usingFilesUnderDirectory("src/test/resources/wiremock/rapidex"));

        wireMockProductCatalog = new WireMockServer(options()
                .templatingEnabled(true)
                .port(8781)
                .usingFilesUnderDirectory("src/test/resources/wiremock/product-catalog"));

        wireMockRapidex.start();
        wireMockProductCatalog.start();
    }

    protected static void stopMock() {
        wireMockRapidex.stop();
        wireMockProductCatalog.stop();
    }
}
