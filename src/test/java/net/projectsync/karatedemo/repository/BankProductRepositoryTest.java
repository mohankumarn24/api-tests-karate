package net.projectsync.karatedemo.repository;

import net.projectsync.karatedemo.model.BankProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.default_schema="
})
@DisplayName("BankProduct Repository Tests")
class BankProductRepositoryTest {

    @Autowired
    private BankProductRepository bankProductRepository;

    @Autowired
    private TestEntityManager entityManager;

    private BankProduct testProduct1;
    private BankProduct testProduct2;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        bankProductRepository.deleteAll();
        entityManager.clear();

        // Initialize test data
        testProduct1 = new BankProduct("Savings Account");
        testProduct2 = new BankProduct("Credit Card");
    }

    @Test
    @DisplayName("Should save bank product successfully")
    void testSaveBankProduct() {
        // When
        BankProduct savedProduct = bankProductRepository.save(testProduct1);

        // Then
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getTitle()).isEqualTo("Savings Account");
    }

    @Test
    @DisplayName("Should find bank product by ID")
    void testFindById() {
        // Given
        BankProduct savedProduct = entityManager.persistAndFlush(testProduct1);
        entityManager.clear();

        // When
        Optional<BankProduct> foundProduct = bankProductRepository.findById(savedProduct.getId());

        // Then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getId()).isEqualTo(savedProduct.getId());
        assertThat(foundProduct.get().getTitle()).isEqualTo("Savings Account");
    }

    @Test
    @DisplayName("Should return empty when bank product not found by ID")
    void testFindByIdNotFound() {
        // When
        Optional<BankProduct> foundProduct = bankProductRepository.findById(999L);

        // Then
        assertThat(foundProduct).isEmpty();
    }

    @Test
    @DisplayName("Should find all bank products")
    void testFindAll() {
        // Given
        entityManager.persist(testProduct1);
        entityManager.persist(testProduct2);
        entityManager.flush();

        // When
        List<BankProduct> products = bankProductRepository.findAll();

        // Then
        assertThat(products).hasSize(2);
        assertThat(products).extracting(BankProduct::getTitle)
                .containsExactlyInAnyOrder("Savings Account", "Credit Card");
    }

    @Test
    @DisplayName("Should return empty list when no bank products exist")
    void testFindAllEmpty() {
        // When
        List<BankProduct> products = bankProductRepository.findAll();

        // Then
        assertThat(products).isEmpty();
    }

    @Test
    @DisplayName("Should update bank product successfully")
    void testUpdateBankProduct() {
        // Given
        BankProduct savedProduct = entityManager.persistAndFlush(testProduct1);
        entityManager.clear();

        // When
        savedProduct.setTitle("Updated Savings Account");
        BankProduct updatedProduct = bankProductRepository.save(savedProduct);

        // Then
        assertThat(updatedProduct.getId()).isEqualTo(savedProduct.getId());
        assertThat(updatedProduct.getTitle()).isEqualTo("Updated Savings Account");
    }

    @Test
    @DisplayName("Should delete bank product by ID")
    void testDeleteById() {
        // Given
        BankProduct savedProduct = entityManager.persistAndFlush(testProduct1);
        Long productId = savedProduct.getId();
        entityManager.clear();

        // When
        bankProductRepository.deleteById(productId);

        // Then
        Optional<BankProduct> deletedProduct = bankProductRepository.findById(productId);
        assertThat(deletedProduct).isEmpty();
    }

    @Test
    @DisplayName("Should delete bank product entity")
    void testDelete() {
        // Given
        BankProduct savedProduct = entityManager.persistAndFlush(testProduct1);
        Long productId = savedProduct.getId();
        entityManager.clear();

        // When
        bankProductRepository.delete(savedProduct);

        // Then
        Optional<BankProduct> deletedProduct = bankProductRepository.findById(productId);
        assertThat(deletedProduct).isEmpty();
    }

    @Test
    @DisplayName("Should delete all bank products")
    void testDeleteAll() {
        // Given
        entityManager.persist(testProduct1);
        entityManager.persist(testProduct2);
        entityManager.flush();

        // When
        bankProductRepository.deleteAll();

        // Then
        List<BankProduct> products = bankProductRepository.findAll();
        assertThat(products).isEmpty();
    }

    @Test
    @DisplayName("Should check if bank product exists by ID")
    void testExistsById() {
        // Given
        BankProduct savedProduct = entityManager.persistAndFlush(testProduct1);

        // When
        boolean exists = bankProductRepository.existsById(savedProduct.getId());
        boolean notExists = bankProductRepository.existsById(999L);

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should count bank products")
    void testCount() {
        // Given
        entityManager.persist(testProduct1);
        entityManager.persist(testProduct2);
        entityManager.flush();

        // When
        long count = bankProductRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should save and flush bank product")
    void testSaveAndFlush() {
        // When
        BankProduct savedProduct = bankProductRepository.saveAndFlush(testProduct1);

        // Then
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isNotNull();

        // Verify it's immediately available in database
        BankProduct foundProduct = entityManager.find(BankProduct.class, savedProduct.getId());
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getTitle()).isEqualTo("Savings Account");
    }

    @Test
    @DisplayName("Should handle null title")
    void testSaveWithNullTitle() {
        // Given
        BankProduct productWithNullTitle = new BankProduct();
        productWithNullTitle.setTitle(null);

        // When
        BankProduct savedProduct = bankProductRepository.save(productWithNullTitle);

        // Then
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getTitle()).isNull();
    }

    @Test
    @DisplayName("Should save multiple bank products")
    void testSaveAll() {
        // Given
        List<BankProduct> products = List.of(testProduct1, testProduct2);

        // When
        List<BankProduct> savedProducts = bankProductRepository.saveAll(products);

        // Then
        assertThat(savedProducts).hasSize(2);
        assertThat(savedProducts).allMatch(p -> p.getId() != null);
        assertThat(savedProducts).extracting(BankProduct::getTitle)
                .containsExactlyInAnyOrder("Savings Account", "Credit Card");
    }
}