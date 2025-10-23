package net.projectsync.karatedemo.controller;

import net.projectsync.karatedemo.model.BankProduct;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import javax.transaction.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Transactional  // Add @Transactional and @Rollback at the class level or method level (These 2 annotations are optional)
@Rollback       // Each test method runs in a transaction. After the test finishes, the transaction is rolled back automatically. Ensures complete isolation between tests
class BankProductControllerRestTemplateIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * @Testcontainers                          -> This JUnit 5 extension will take care of starting the container before tests and stopping it after tests
     * If the container is a static field       -> container will be started once before all the tests and stopped after all the tests (all testes share this container)
     * If the container is a non-static field   -> container will be started before each test and stopped after each test
     *
     * if we use @ServiceConnection, no need of @DynamicPropertySource
     */
    @Container
    // @ServiceConnection
    private static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("karate_test")
            .withUsername("postgres")
            .withPassword("Password1")
            .withInitScript("schema.sql");  // This was added to avoid 'schema not found' error. This file can be used to create db, schema, tables, db init, etc

    @DynamicPropertySource
    private static void overrideProps(DynamicPropertyRegistry registry) {
            registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
            registry.add("spring.datasource.username", postgresContainer::getUsername);
            registry.add("spring.datasource.password", postgresContainer::getPassword);
            registry.add("spring.jpa.properties.hibernate.default_schema", () -> "karate");
            registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
            registry.add("spring.jpa.show-sql", () -> "true");
    }

    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1/bankproducts";
        // or simply return "/api/v1/bankproducts"
    }

    // CREATE
    @Test
    void testCreateProduct() {

        BankProduct bankProduct = new BankProduct("Fixed Deposit");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Accept", "application/json");
        HttpEntity<BankProduct> request = new HttpEntity<>(bankProduct, httpHeaders);

        ResponseEntity<BankProduct> response = restTemplate.postForEntity(baseUrl(), request, BankProduct.class);

        // Assert HTTP status
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Assert response body
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isNotNull();
        Assertions.assertThat(response.getBody().getTitle()).isEqualTo("Fixed Deposit");
        /*
         * assertEquals: simple unit tests with primitives or simple objects
         * AssertJ: integration tests, API responses, collections, nested objects, and for better readability
         */
    }

    // READ (by ID)
    @Test
    void testGetProductById() {

        // First create a product
        BankProduct bankProduct = restTemplate.postForEntity(baseUrl(), new BankProduct("Savings Account"), BankProduct.class).getBody();
        assertThat(bankProduct).isNotNull();

        ResponseEntity<BankProduct> response = restTemplate.getForEntity(baseUrl() + "/" + bankProduct.getId(), BankProduct.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getTitle()).isEqualTo("Savings Account");
    }

    // READ (by invalid ID)
    @Test
    void testGetProductByInvalidIdException() {

        // Create one valid product (optional)
        BankProduct bankProduct = restTemplate.postForEntity(baseUrl(), new BankProduct("Savings Account"), BankProduct.class).getBody();
        assertThat(bankProduct).isNotNull();

        // Hit endpoint with invalid ID (string instead of number)
        ResponseEntity<BankProduct> response = restTemplate.getForEntity(baseUrl() + "/" + "invalidId", BankProduct.class);

        // Assert HTTP 400
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).isNotNull();  // response.getBody() -> BankProduct(id=null, title=null)
    }

    // READ (all products)
    @Test
    void testGetAllProducts() {

        // Create multiple products
        restTemplate.postForEntity(baseUrl(), new BankProduct("Product 1"), BankProduct.class);
        restTemplate.postForEntity(baseUrl(), new BankProduct("Product 2"), BankProduct.class);

        ResponseEntity<BankProduct[]> response = restTemplate.getForEntity(baseUrl(), BankProduct[].class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotEmpty();
        Assertions.assertThat(response.getBody().length).isGreaterThanOrEqualTo(2);
    }

    // UPDATE
    @Test
    void testUpdateProduct() {

        // Create a product first
        BankProduct bankProduct = restTemplate.postForEntity(baseUrl(), new BankProduct("Old Title"), BankProduct.class).getBody();
        assertThat(bankProduct).isNotNull();

        // Prepare update
        bankProduct.setTitle("New Title");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BankProduct> request = new HttpEntity<>(bankProduct, httpHeaders);

        ResponseEntity<BankProduct> response = restTemplate.exchange(baseUrl() + "/" + bankProduct.getId(), HttpMethod.PUT, request, BankProduct.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getTitle()).isEqualTo("New Title");
    }

    // DELETE using exchange() (industry standard)
    @Test
    void testDeleteProductUsingExchange() {

        BankProduct bankProduct = restTemplate.postForEntity(baseUrl(), new BankProduct("To Be Deleted"), BankProduct.class).getBody();
        assertThat(bankProduct).isNotNull();

        ResponseEntity<Void> response = restTemplate.exchange(baseUrl() + "/" + bankProduct.getId(), HttpMethod.DELETE, null, Void.class);

        // Assert DELETE HTTP status
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Confirm deletion
        ResponseEntity<BankProduct> checkResponse = restTemplate.getForEntity(baseUrl() + "/" + bankProduct.getId(), BankProduct.class);
        Assertions.assertThat(checkResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // DELETE using restTemplate.delete() (simpler)
    @Test
    void testDeleteProductUsingDelete() {

        BankProduct bankProduct = restTemplate.postForEntity(baseUrl(), new BankProduct("Delete Simple"), BankProduct.class).getBody();
        Assertions.assertThat(bankProduct).isNotNull();

        // Simple delete
        restTemplate.delete(baseUrl() + "/" + bankProduct.getId());

        // Confirm deletion
        ResponseEntity<BankProduct> checkResponse = restTemplate.getForEntity(baseUrl() + "/" + bankProduct.getId(), BankProduct.class);
        Assertions.assertThat(checkResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
