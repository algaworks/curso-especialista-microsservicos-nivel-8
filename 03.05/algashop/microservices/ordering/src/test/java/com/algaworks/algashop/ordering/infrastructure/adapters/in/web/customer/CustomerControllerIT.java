package com.algaworks.algashop.ordering.infrastructure.adapters.in.web.customer;

import com.algaworks.algashop.ordering.infrastructure.adapters.in.web.AbstractPresentationIT;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.utils.AlgaShopResourceUtils;
import com.algaworks.algashop.ordering.utils.MockJwtFactory;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

public class CustomerControllerIT extends AbstractPresentationIT {

    @Autowired
    private CustomerPersistenceEntityRepository customerRepository;

    private static final UUID validCustomerId = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");

    @BeforeEach
    public void setup() {
        super.beforeEach();
    }

    @BeforeAll
    public static void setupBeforeAll() {
        AbstractPresentationIT.initWireMock();
    }

    @AfterAll
    public static void afterAll() {
        AbstractPresentationIT.stopMock();
    }

    @Test
    public void shouldCreateMyCustomerProfile() {
        String json = AlgaShopResourceUtils.readContent("json/create-customer.json");

        UUID createdCustomerId = givenAuthenticatedUnknownCustomer()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
            .when()
                .post("/api/v1/customers/me")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", Matchers.containsString("/api/v1/customers/me"))
                .body("id", Matchers.is(MockJwtFactory.UNKNOWN_CUSTOMER_SUBJECT))
                .extract()
            .jsonPath().getUUID("id");

        Assertions.assertThat(customerRepository.existsById(createdCustomerId)).isTrue();
    }

    @Test
    public void shouldLoadMyCustomerProfile() {
        givenAuthenticated()
                .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/api/v1/customers/me")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.is(validCustomerId.toString()));
    }

    @Test
    public void shouldUpdateMyCustomerProfile() {
        givenAuthenticated()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(updateCustomerJson())
            .when()
                .put("/api/v1/customers/me")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.is(validCustomerId.toString()));

        Assertions.assertThat(customerRepository.findById(validCustomerId).orElseThrow().getFirstName())
                .isEqualTo("John");
    }

    @Test
    public void shouldListCustomersWhenAuthenticatedAsAdmin() {
        givenAuthenticatedAdmin()
                .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/api/v1/customers")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void shouldReturnForbiddenWhenListingCustomersAsCustomer() {
        givenAuthenticated()
                .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get("/api/v1/customers")
            .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void shouldReturnForbiddenWhenCreatingMyCustomerProfileWithoutWriteScope() {
        String json = AlgaShopResourceUtils.readContent("json/create-customer.json");

        givenAuthenticatedWithNoScopeToken()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
            .when()
                .post("/api/v1/customers/me")
            .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void shouldReturnUnauthorizedWhenExpiredTokenIsGiven() {
        String json = AlgaShopResourceUtils.readContent("json/create-customer.json");

        givenWithExpiredToken()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
            .when()
                .post("/api/v1/customers/me")
            .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    private String updateCustomerJson() {
        return """
        {
          "firstName": "John",
          "lastName": "Doe",
          "phone": "1191234564",
          "promotionNotificationsAllowed": false,
          "address": {
            "street": "Bourbon Street",
            "number": "2000",
            "complement": "apt 122",
            "neighborhood": "North Ville",
            "city": "Yostfort",
            "state": "South Carolina",
            "zipCode": "12321"
          }
        }
        """;
    }
}
