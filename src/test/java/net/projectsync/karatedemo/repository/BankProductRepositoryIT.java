package net.projectsync.karatedemo.repository;

import net.projectsync.karatedemo.model.BankProduct;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@Testcontainers
class BankProductRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("karate_test")
            .withUsername("postgres")
            .withPassword("Password1")
            .withInitScript("schema.sql"); // Optional: initial schema setup

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.jpa.properties.hibernate.default_schema", () -> "karate");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "true");
    }

    @Autowired
    private BankProductRepository repository;

    // CREATE & FIND by ID
    @Test
    void testSaveAndFindById() {

        BankProduct product = new BankProduct("Savings Account");
        BankProduct saved = repository.save(product);

        Optional<BankProduct> found = repository.findById(saved.getId());
        Assertions.assertThat(found).isPresent();
        Assertions.assertThat(found.get().getTitle()).isEqualTo("Savings Account");
    }

    // FIND all
    @Test
    void testFindAll() {

        repository.save(new BankProduct("Product 1"));
        repository.save(new BankProduct("Product 2"));

        List<BankProduct> allProducts = repository.findAll();
        Assertions.assertThat(allProducts)
                .isNotEmpty()
                .hasSizeGreaterThanOrEqualTo(2);
    }

    // UPDATE
    @Test
    void testUpdate() {

        BankProduct product = repository.save(new BankProduct("Old Title"));

        // Update title
        product.setTitle("New Title");
        BankProduct updated = repository.save(product);

        Optional<BankProduct> found = repository.findById(updated.getId());
        Assertions.assertThat(found).isPresent();
        Assertions.assertThat(found.get().getTitle()).isEqualTo("New Title");
    }

    // DELETE by ID
    @Test
    void testDeleteById() {

        BankProduct product = repository.save(new BankProduct("To Be Deleted"));
        Long id = product.getId();

        repository.deleteById(id);
        Assertions.assertThat(repository.findById(id)).isEmpty();
    }

    // DELETE all
    @Test
    void testDeleteAll() {

        repository.save(new BankProduct("Product A"));
        repository.save(new BankProduct("Product B"));

        repository.deleteAll();

        List<BankProduct> remaining = repository.findAll();
        Assertions.assertThat(remaining).isEmpty();
    }

    // FIND by non-existing ID
    @Test
    void testFindByNonExistingId() {

        Optional<BankProduct> found = repository.findById(999L);
        Assertions.assertThat(found).isEmpty();
    }
}
