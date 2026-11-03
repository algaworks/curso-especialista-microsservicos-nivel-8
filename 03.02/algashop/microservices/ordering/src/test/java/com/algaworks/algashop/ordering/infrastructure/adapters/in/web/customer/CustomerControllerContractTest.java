package com.algaworks.algashop.ordering.infrastructure.adapters.in.web.customer;

import com.algaworks.algashop.ordering.core.application.customer.CustomerOutputTestDataBuilder;
import com.algaworks.algashop.ordering.core.application.customer.CustomerSummaryOutputTestDataBuilder;
import com.algaworks.algashop.ordering.core.application.security.SecurityChecks;
import com.algaworks.algashop.ordering.core.domain.model.DomainException;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerEmailIsInUseException;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.core.ports.in.commons.AddressData;
import com.algaworks.algashop.ordering.core.ports.in.customer.CustomerFilter;
import com.algaworks.algashop.ordering.core.ports.in.customer.CustomerInput;
import com.algaworks.algashop.ordering.core.ports.in.customer.CustomerOutput;
import com.algaworks.algashop.ordering.core.ports.in.customer.CustomerSummaryOutput;
import com.algaworks.algashop.ordering.core.ports.in.customer.CustomerUpdateInput;
import com.algaworks.algashop.ordering.core.ports.in.customer.ForManagingCustomers;
import com.algaworks.algashop.ordering.core.ports.in.customer.ForQueryingCustomers;
import com.algaworks.algashop.ordering.core.ports.in.shoppingcart.ForQueryingShoppingCarts;
import com.algaworks.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartOutput;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@WebMvcTest(controllers = {CustomerController.class, MyCustomerController.class})
class CustomerControllerContractTest {

    private static final UUID AUTHENTICATED_CUSTOMER_ID = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private ForManagingCustomers customerManagementService;

    @MockitoBean
    private ForQueryingCustomers customerQueryService;

    @MockitoBean
    private ForQueryingShoppingCarts shoppingCartQueryService;

    @MockitoBean
    private SecurityChecks securityChecks;

    @BeforeEach
    public void setupAll() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build());
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();

        Mockito.when(securityChecks.getAuthenticatedUserId()).thenReturn(AUTHENTICATED_CUSTOMER_ID);
    }

    @Test
    public void createMyCustomerProfileContract() {
        CustomerOutput customerOutput = CustomerOutputTestDataBuilder.existing()
                .id(AUTHENTICATED_CUSTOMER_ID)
                .build();

        Mockito.when(customerManagementService.create(Mockito.eq(AUTHENTICATED_CUSTOMER_ID), Mockito.any(CustomerInput.class)))
                .thenReturn(AUTHENTICATED_CUSTOMER_ID);
        Mockito.when(customerQueryService.findById(AUTHENTICATED_CUSTOMER_ID))
                .thenReturn(customerOutput);

        RestAssuredMockMvc
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(validCustomerInputJson())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .post("/api/v1/customers/me")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", Matchers.containsString("/api/v1/customers/me"))
                .body(
                        "id", Matchers.is(AUTHENTICATED_CUSTOMER_ID.toString()),
                        "registeredAt", Matchers.notNullValue(),
                        "firstName", Matchers.is("John"),
                        "lastName", Matchers.is("Doe"),
                        "email", Matchers.is("johndoe@email.com"),
                        "document", Matchers.is("12345"),
                        "phone", Matchers.is("1191234564"),
                        "birthDate", Matchers.is("1991-07-05"),
                        "promotionNotificationsAllowed", Matchers.is(false),
                        "loyaltyPoints", Matchers.is(0),
                        "address.street", Matchers.is("Bourbon Street"),
                        "address.number", Matchers.is("2000"),
                        "address.complement", Matchers.is("apt 122"),
                        "address.neighborhood", Matchers.is("North Ville"),
                        "address.city", Matchers.is("Yostfort"),
                        "address.state", Matchers.is("South Carolina"),
                        "address.zipCode", Matchers.is("12321")
                );
    }

    @Test
    public void createMyCustomerProfileError400Contract() {
        RestAssuredMockMvc
            .given()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(invalidCustomerInputJson())
            .when()
                .post("/api/v1/customers/me")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.BAD_REQUEST.value())
            .body(
                    "status", Matchers.is(HttpStatus.BAD_REQUEST.value()),
                    "type", Matchers.is("/errors/invalid-fields"),
                    "title", Matchers.notNullValue(),
                    "detail", Matchers.notNullValue(),
                    "instance", Matchers.notNullValue(),
                    "fields", Matchers.notNullValue()
            );
    }

    @Test
    public void loadMyCustomerProfileContract() {
        CustomerOutput customer = CustomerOutputTestDataBuilder.existing()
                .id(AUTHENTICATED_CUSTOMER_ID)
                .build();

        Mockito.when(customerQueryService.findById(AUTHENTICATED_CUSTOMER_ID)).thenReturn(customer);

        assertCustomerResponse(
                RestAssuredMockMvc
                        .given()
                            .accept(MediaType.APPLICATION_JSON)
                        .when()
                            .get("/api/v1/customers/me")
                        .then()
                            .assertThat()
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .statusCode(HttpStatus.OK.value()),
                customer
        );
    }

    @Test
    public void updateMyCustomerProfileContract() {
        CustomerOutput customer = CustomerOutputTestDataBuilder.existing()
                .id(AUTHENTICATED_CUSTOMER_ID)
                .build();

        Mockito.when(customerQueryService.findById(AUTHENTICATED_CUSTOMER_ID)).thenReturn(customer);

        assertCustomerResponse(
                RestAssuredMockMvc
                    .given()
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .body(validCustomerUpdateInputJson())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                        .put("/api/v1/customers/me")
                    .then()
                        .assertThat()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .statusCode(HttpStatus.OK.value()),
                customer
        );

        Mockito.verify(customerManagementService)
                .update(Mockito.eq(AUTHENTICATED_CUSTOMER_ID), Mockito.any(CustomerUpdateInput.class));
    }

    @Test
    public void findCustomersContract() {
        int sizeLimit = 5;
        int pageNumber = 0;

        CustomerSummaryOutput customer1 = CustomerSummaryOutputTestDataBuilder.existing().build();
        CustomerSummaryOutput customer2 = CustomerSummaryOutputTestDataBuilder.existingAlt1().build();

        Mockito.when(customerQueryService.filter(Mockito.any(CustomerFilter.class)))
                .thenReturn(new PageImpl<>(List.of(customer1, customer2)));

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        RestAssuredMockMvc
            .given()
                .accept(MediaType.APPLICATION_JSON)
                .queryParam("size", sizeLimit)
                .queryParam("page", pageNumber)
            .when()
                .get("/api/v1/customers")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body(
                        "number", Matchers.equalTo(pageNumber),
                        "size", Matchers.equalTo(2),
                        "totalPages", Matchers.equalTo(1),
                        "totalElements", Matchers.equalTo(2),
                        "content[0].id", Matchers.equalTo(customer1.getId().toString()),
                        "content[0].firstName", Matchers.is(customer1.getFirstName()),
                        "content[0].lastName", Matchers.is(customer1.getLastName()),
                        "content[0].email", Matchers.is(customer1.getEmail()),
                        "content[0].document", Matchers.is(customer1.getDocument()),
                        "content[0].phone", Matchers.is(customer1.getPhone()),
                        "content[0].birthDate", Matchers.is(customer1.getBirthDate().toString()),
                        "content[0].loyaltyPoints", Matchers.is(customer1.getLoyaltyPoints()),
                        "content[0].promotionNotificationsAllowed", Matchers.is(customer1.getPromotionNotificationsAllowed()),
                        "content[0].archived", Matchers.is(customer1.getArchived()),
                        "content[0].registeredAt", Matchers.is(formatter.format(customer1.getRegisteredAt())),
                        "content[1].id", Matchers.equalTo(customer2.getId().toString()),
                        "content[1].firstName", Matchers.is(customer2.getFirstName()),
                        "content[1].lastName", Matchers.is(customer2.getLastName()),
                        "content[1].email", Matchers.is(customer2.getEmail()),
                        "content[1].document", Matchers.is(customer2.getDocument()),
                        "content[1].phone", Matchers.is(customer2.getPhone()),
                        "content[1].birthDate", Matchers.is(customer2.getBirthDate().toString()),
                        "content[1].loyaltyPoints", Matchers.is(customer2.getLoyaltyPoints()),
                        "content[1].promotionNotificationsAllowed", Matchers.is(customer2.getPromotionNotificationsAllowed()),
                        "content[1].archived", Matchers.is(customer2.getArchived()),
                        "content[1].registeredAt", Matchers.is(formatter.format(customer2.getRegisteredAt()))
                );
    }

    @Test
    public void findByIdContract() {
        CustomerOutput customer = CustomerOutputTestDataBuilder.existing().build();

        Mockito.when(customerQueryService.findById(customer.getId())).thenReturn(customer);

        assertCustomerResponse(
                RestAssuredMockMvc
                        .given()
                            .accept(MediaType.APPLICATION_JSON)
                        .when()
                            .get("/api/v1/customers/{customerId}", customer.getId())
                        .then()
                            .assertThat()
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .statusCode(HttpStatus.OK.value()),
                customer
        );
    }

    @Test
    public void findByIdError404Contract() {
        UUID invalidCustomerId = UUID.randomUUID();

        Mockito.when(customerQueryService.findById(invalidCustomerId))
                .thenThrow(CustomerNotFoundException.class);

        RestAssuredMockMvc
            .given()
                .accept(MediaType.APPLICATION_JSON)
            .when()
                .get("/api/v1/customers/{customerId}", invalidCustomerId)
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(
                        "status", Matchers.is(HttpStatus.NOT_FOUND.value()),
                        "type", Matchers.is("/errors/not-found"),
                        "title", Matchers.notNullValue(),
                        "instance", Matchers.notNullValue()
                );
    }

    @Test
    public void findShoppingCartByCustomerIdContract() {
        UUID customerId = UUID.randomUUID();
        UUID shoppingCartId = UUID.randomUUID();

        Mockito.when(shoppingCartQueryService.findByCustomerId(customerId))
                .thenReturn(ShoppingCartOutput.builder()
                        .id(shoppingCartId)
                        .customerId(customerId)
                        .totalItems(0)
                        .totalAmount(BigDecimal.ZERO)
                        .build());

        RestAssuredMockMvc
            .given()
                .accept(MediaType.APPLICATION_JSON)
            .when()
                .get("/api/v1/customers/{customerId}/shopping-cart", customerId)
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.OK.value())
                .body(
                        "id", Matchers.is(shoppingCartId.toString()),
                        "customerId", Matchers.is(customerId.toString()),
                        "totalItems", Matchers.is(0),
                        "totalAmount", Matchers.is(0)
                );
    }

    @Test
    public void createMyCustomerProfileError409Contract() {
        Mockito.when(customerManagementService.create(Mockito.eq(AUTHENTICATED_CUSTOMER_ID), Mockito.any(CustomerInput.class)))
                .thenThrow(CustomerEmailIsInUseException.class);

        assertProblemResponseForCreate(HttpStatus.CONFLICT, "/errors/conflict");
    }

    @Test
    public void createMyCustomerProfileError422Contract() {
        Mockito.when(customerManagementService.create(Mockito.eq(AUTHENTICATED_CUSTOMER_ID), Mockito.any(CustomerInput.class)))
                .thenThrow(DomainException.class);

        assertProblemResponseForCreate(HttpStatus.UNPROCESSABLE_CONTENT, "/errors/unprocessable-entity");
    }

    @Test
    public void createMyCustomerProfileError500Contract() {
        Mockito.when(customerManagementService.create(Mockito.eq(AUTHENTICATED_CUSTOMER_ID), Mockito.any(CustomerInput.class)))
                .thenThrow(RuntimeException.class);

        assertProblemResponseForCreate(HttpStatus.INTERNAL_SERVER_ERROR, "/errors/internal");
    }

    private void assertProblemResponseForCreate(HttpStatus status, String type) {
        RestAssuredMockMvc
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(validCustomerInputJson())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .post("/api/v1/customers/me")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(status.value())
                .body(
                        "status", Matchers.is(status.value()),
                        "type", Matchers.is(type),
                        "title", Matchers.notNullValue(),
                        "instance", Matchers.notNullValue()
                );
    }

    private void assertCustomerResponse(io.restassured.module.mockmvc.response.ValidatableMockMvcResponse response,
                                        CustomerOutput customer) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        AddressData address = customer.getAddress();

        response.body(
                "id", Matchers.equalTo(customer.getId().toString()),
                "firstName", Matchers.equalTo(customer.getFirstName()),
                "lastName", Matchers.is(customer.getLastName()),
                "email", Matchers.is(customer.getEmail()),
                "document", Matchers.is(customer.getDocument()),
                "phone", Matchers.is(customer.getPhone()),
                "birthDate", Matchers.is(customer.getBirthDate().toString()),
                "loyaltyPoints", Matchers.is(customer.getLoyaltyPoints()),
                "promotionNotificationsAllowed", Matchers.is(customer.getPromotionNotificationsAllowed()),
                "archived", Matchers.is(customer.getArchived()),
                "registeredAt", Matchers.is(formatter.format(customer.getRegisteredAt())),
                "address.street", Matchers.is(address.getStreet()),
                "address.number", Matchers.is(address.getNumber()),
                "address.complement", Matchers.is(address.getComplement()),
                "address.neighborhood", Matchers.is(address.getNeighborhood()),
                "address.city", Matchers.is(address.getCity()),
                "address.state", Matchers.is(address.getState()),
                "address.zipCode", Matchers.is(address.getZipCode())
        );
    }

    private String validCustomerInputJson() {
        return """
        {
          "firstName": "John",
          "lastName": "Doe",
          "email": "johndoe@email.com",
          "document": "12345",
          "phone": "1191234564",
          "birthDate": "1991-07-05",
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

    private String invalidCustomerInputJson() {
        return """
        {
          "firstName": "",
          "lastName": "",
          "email": "johndoe@email.com",
          "document": "12345",
          "phone": "1191234564",
          "birthDate": "1991-07-05",
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

    private String validCustomerUpdateInputJson() {
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
