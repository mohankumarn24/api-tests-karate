package net.projectsync.karatedemo.controller;

import net.projectsync.karatedemo.model.BankProduct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class BankProductControllerRestTemplateIT {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("karate_test")
            .withUsername("postgres")
            .withPassword("Password1")
            .withInitScript("schema.sql");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.jpa.properties.hibernate.default_schema", () -> "karate");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "true");
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    // Helper method to construct base URL
    private String baseUrl() {
        return "http://localhost:" + port + "/bankproducts";
        // or simply return "/bankproducts"
    }

    // ----------------------------
    // CREATE
    // ----------------------------
    @Test
    void testCreateProduct() {
        BankProduct product = new BankProduct("Fixed Deposit");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BankProduct> request = new HttpEntity<>(product, headers);

        ResponseEntity<BankProduct> response = restTemplate.postForEntity(baseUrl(), request, BankProduct.class);

        // Assert HTTP status
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Assert response body
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Fixed Deposit");
    }

    // ----------------------------
    // READ (by ID)
    // ----------------------------
    @Test
    void testGetProductById() {
        // First create a product
        BankProduct product = restTemplate.postForEntity(baseUrl(), new BankProduct("Savings Account"), BankProduct.class).getBody();
        assertThat(product).isNotNull();

        ResponseEntity<BankProduct> response = restTemplate.getForEntity(baseUrl() + "/" + product.getId(), BankProduct.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Savings Account");
    }

    // ----------------------------
    // READ (all products)
    // ----------------------------
    @Test
    void testGetAllProducts() {
        // Create multiple products
        restTemplate.postForEntity(baseUrl(), new BankProduct("Product 1"), BankProduct.class);
        restTemplate.postForEntity(baseUrl(), new BankProduct("Product 2"), BankProduct.class);

        ResponseEntity<BankProduct[]> response = restTemplate.getForEntity(baseUrl(), BankProduct[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(2);
    }

    // ----------------------------
    // UPDATE
    // ----------------------------
    @Test
    void testUpdateProduct() {
        // Create a product first
        BankProduct product = restTemplate.postForEntity(baseUrl(), new BankProduct("Old Title"), BankProduct.class).getBody();
        assertThat(product).isNotNull();

        // Prepare update
        product.setTitle("New Title");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BankProduct> request = new HttpEntity<>(product, headers);

        ResponseEntity<BankProduct> response = restTemplate.exchange(
                baseUrl() + "/" + product.getId(),
                HttpMethod.PUT,
                request,
                BankProduct.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTitle()).isEqualTo("New Title");
    }

    // ----------------------------
    // DELETE using exchange() (industry standard)
    // ----------------------------
    @Test
    void testDeleteProductUsingExchange() {
        BankProduct product = restTemplate.postForEntity(baseUrl(), new BankProduct("To Be Deleted"), BankProduct.class).getBody();
        assertThat(product).isNotNull();

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl() + "/" + product.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // Assert DELETE HTTP status
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Confirm deletion
        ResponseEntity<BankProduct> checkResponse = restTemplate.getForEntity(baseUrl() + "/" + product.getId(), BankProduct.class);
        assertThat(checkResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ----------------------------
    // DELETE using restTemplate.delete() (simpler)
    // ----------------------------
    @Test
    void testDeleteProductUsingDelete() {
        BankProduct product = restTemplate.postForEntity(baseUrl(), new BankProduct("Delete Simple"), BankProduct.class).getBody();
        assertThat(product).isNotNull();

        // Simple delete
        restTemplate.delete(baseUrl() + "/" + product.getId());

        // Confirm deletion
        ResponseEntity<BankProduct> checkResponse = restTemplate.getForEntity(baseUrl() + "/" + product.getId(), BankProduct.class);
        assertThat(checkResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
