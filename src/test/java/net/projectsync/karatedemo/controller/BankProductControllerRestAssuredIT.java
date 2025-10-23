package net.projectsync.karatedemo.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.projectsync.karatedemo.model.BankProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class BankProductControllerRestAssuredIT {

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

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    // CREATE
    @Test
    void testCreateProduct() {

        BankProduct product = new BankProduct("Recurring Deposit");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/api/v1/bankproducts")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo("Recurring Deposit"));
    }

    // READ by ID
    @Test
    void testGetProductById() {

        // First create a product - extract as Long
        Long id = RestAssured.given()
                        .contentType(ContentType.JSON)
                        .body(new BankProduct("Savings Account"))
                        .when()
                        .post("/api/v1/bankproducts")
                        .then()
                        .statusCode(201)
                        .extract()
                        .jsonPath()
                        .getLong("id");

        // GET by ID
        RestAssured.given()
                .when()
                .get("/api/v1/bankproducts/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("title", equalTo("Savings Account"));
    }

    // READ all products
    @Test
    void testGetAllProducts() {

        // Create multiple products
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new BankProduct("Product 1"))
                .post("/api/v1/bankproducts");
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new BankProduct("Product 2"))
                .post("/api/v1/bankproducts");

        // GET all
        RestAssured.given()
                .when()
                .get("/api/v1/bankproducts")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(2))
                .body("title", hasItems("Product 1", "Product 2"));
    }

    // UPDATE
    @Test
    void testUpdateProduct() {

        // Create a product first - extract as Long
        Long id = RestAssured.given()
                        .contentType(ContentType.JSON)
                        .body(new BankProduct("Old Title"))
                        .when()
                        .post("/api/v1/bankproducts")
                        .then()
                        .statusCode(201)
                        .extract()
                        .jsonPath()
                        .getLong("id");

        // Prepare update
        BankProduct updated = new BankProduct("New Title");

        // PUT request
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(updated)
                .when()
                .put("/api/v1/bankproducts/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("title", equalTo("New Title"));
    }

    // DELETE
    @Test
    void testDeleteProduct() {

        // Create a product first - extract as Long
        Long id = RestAssured.given()
                        .contentType(ContentType.JSON)
                        .body(new BankProduct("To Be Deleted"))
                        .when()
                        .post("/api/v1/bankproducts")
                        .then()
                        .statusCode(201)
                        .extract()
                        .jsonPath()
                        .getLong("id");

        // DELETE
        RestAssured.given()
                .when()
                .delete("/api/v1/bankproducts/{id}", id)
                .then()
                .statusCode(204);

        // Confirm deletion
        RestAssured.given()
                .when()
                .get("/api/v1/bankproducts/{id}", id)
                .then()
                .statusCode(404);
    }

    // Additional Test Cases
    @Test
    void testGetProductByIdNotFound() {

        RestAssured.given()
                .when()
                .get("/api/v1/bankproducts/{id}", 99999)
                .then()
                .statusCode(404);
    }

    @Test
    void testUpdateProductNotFound() {

        BankProduct updated = new BankProduct("Non-existent Product");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(updated)
                .when()
                .put("/api/v1/bankproducts/{id}", 99999)
                .then()
                .statusCode(404);
    }

    @Test
    void testDeleteProductNotFound() {

        RestAssured.given()
                .when()
                .delete("/api/v1/bankproducts/{id}", 99999)
                .then()
                .statusCode(404);
    }

    @Test
    void testCreateProductWithNullTitle() {

        BankProduct product = new BankProduct();
        product.setTitle(null);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/api/v1/bankproducts")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", nullValue());
    }

    @Test
    void testCreateProductWithEmptyTitle() {

        BankProduct product = new BankProduct("");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/api/v1/bankproducts")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo(""));
    }
}

/*
 * .getString("title")                      // Extract String
 * .getInt("id")                            // Extract Integer
 * .getLong("id")                           // Extract Long
 * .getBoolean("active")                    // Extract Boolean
 * .getList("items")                        // Extract List
 * .getObject("data", BankProduct.class)    // Extract custom object
 */