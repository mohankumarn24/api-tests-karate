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

    private BankProduct bankProduct1;
    private BankProduct bankProduct2;

    @BeforeEach
    void setUp() {
        bankProduct1 = new BankProduct("Savings Account");
        bankProduct2 = new BankProduct("Credit Card");
    }

    // Important
    @Test
    @DisplayName("Demo: Managed vs Detached fetch")
    void testManagedVsDetachedFetch() {

        // Step 1: Save and flush entity.
        // Saves the entity (like save()). Immediately flushes the persistence context, forcing the SQL INSERT/UPDATE to hit the database
        // So, unlike plain save(), it guarantees the database has the changes before you do any subsequent reads.
        BankProduct saved = bankProductRepository.saveAndFlush(new BankProduct("Demo Account"));

        // Step 2a: Fetch while still managed
        Optional<BankProduct> managedFetch = bankProductRepository.findById(saved.getId());
        // This may NOT hit the database; JPA returns the cached instance
        System.out.println("Managed fetch title: " + managedFetch.get().getTitle());

        // Step 2b: Detach entity
        entityManager.detach(saved);

        // Fetch after detaching (guaranteed from DB)
        Optional<BankProduct> detachedFetch = bankProductRepository.findById(saved.getId());
        System.out.println("Detached fetch title: " + detachedFetch.get().getTitle());
    }

    @Test
    @DisplayName("Should save bank product successfully")
    void testSaveBankProduct() {
        BankProduct saved = bankProductRepository.saveAndFlush(bankProduct1);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Savings Account");
    }

    @Test
    @DisplayName("Should find bank product by ID")
    void testFindById() {
        BankProduct saved = bankProductRepository.saveAndFlush(bankProduct1);

        Optional<BankProduct> found = bankProductRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Savings Account");
    }

    @Test
    @DisplayName("Should return empty when bank product not found by ID")
    void testFindByIdNotFound() {
        Optional<BankProduct> found = bankProductRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find all bank products")
    void testFindAll() {
        bankProductRepository.saveAndFlush(bankProduct1);
        bankProductRepository.saveAndFlush(bankProduct2);

        List<BankProduct> products = bankProductRepository.findAll();

        assertThat(products).hasSize(2)
                .extracting(BankProduct::getTitle)
                .containsExactlyInAnyOrder("Savings Account", "Credit Card");
    }

    @Test
    @DisplayName("Should return empty list when no bank products exist")
    void testFindAllEmpty() {
        List<BankProduct> products = bankProductRepository.findAll();
        assertThat(products).isEmpty();
    }

    @Test
    @DisplayName("Should update bank product successfully")
    void testUpdateBankProduct() {
        BankProduct saved = bankProductRepository.saveAndFlush(bankProduct1);
        saved.setTitle("Updated Savings Account");
        BankProduct updated = bankProductRepository.saveAndFlush(saved);

        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getTitle()).isEqualTo("Updated Savings Account");
    }

    @Test
    @DisplayName("Should delete bank product by ID")
    void testDeleteById() {
        BankProduct saved = bankProductRepository.saveAndFlush(bankProduct1);
        Long id = saved.getId();

        bankProductRepository.deleteById(id);

        assertThat(bankProductRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("Should delete bank product entity")
    void testDelete() {
        BankProduct saved = bankProductRepository.saveAndFlush(bankProduct1);
        Long id = saved.getId();

        bankProductRepository.delete(saved);

        assertThat(bankProductRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("Should delete all bank products")
    void testDeleteAll() {
        bankProductRepository.saveAndFlush(bankProduct1);
        bankProductRepository.saveAndFlush(bankProduct2);

        bankProductRepository.deleteAll();

        assertThat(bankProductRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Should check if bank product exists by ID")
    void testExistsById() {
        BankProduct saved = bankProductRepository.saveAndFlush(bankProduct1);

        assertThat(bankProductRepository.existsById(saved.getId())).isTrue();
        assertThat(bankProductRepository.existsById(999L)).isFalse();
    }

    @Test
    @DisplayName("Should count bank products")
    void testCount() {
        bankProductRepository.saveAndFlush(bankProduct1);
        bankProductRepository.saveAndFlush(bankProduct2);

        assertThat(bankProductRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should handle null title")
    void testSaveWithNullTitle() {
        BankProduct nullTitleProduct = new BankProduct();
        nullTitleProduct.setTitle(null);

        BankProduct saved = bankProductRepository.saveAndFlush(nullTitleProduct);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isNull();
    }

    @Test
    @DisplayName("Should save multiple bank products")
    void testSaveAll() {
        List<BankProduct> products = List.of(bankProduct1, bankProduct2);
        List<BankProduct> savedProducts = bankProductRepository.saveAll(products);

        assertThat(savedProducts).hasSize(2)
                .allMatch(p -> p.getId() != null)
                .extracting(BankProduct::getTitle)
                .containsExactlyInAnyOrder("Savings Account", "Credit Card");
    }
}

/*
JPA save(), saveAndFlush(), and persistAndFlush() in tests

1. repository.save(x)
- Saves the entity using Spring Data JPA.
- The entity remains managed by JPA (in-memory).
- Changes to it are tracked automatically.
- Queries may return the same in-memory instance, not necessarily hitting the database.

✅ Use it when you just want to verify that saving works.

2. repository.saveAndFlush(x)
- Saves the entity and immediately flushes changes to the database.
- The entity is still managed.
- Queries may still return the in-memory instance, but the DB has been updated.

✅ Use it when you want to ensure DB writes happen immediately, but don’t need a fresh DB fetch.

3. entityManager.persistAndFlush(x) + entityManager.clear()
- persistAndFlush() registers the entity and forces SQL INSERT immediately.
- clear() detaches all entities from the persistence context.
- Subsequent queries fetch entities fresh from the database, not from JPA cache.

✅ Use it when you want to test real database retrieval and avoid false positives from caching.

Key difference in tests:

| Method                           | Flush? | Entity detached? | Use case                               |
|---------------------------------|--------|-----------------|---------------------------------------|
| save()                           | Maybe later | No            | Simple save verification              |
| saveAndFlush()                    | Yes    | No              | Ensure DB write, still managed        |
| persistAndFlush() + clear()       | Yes    | Yes             | Test actual DB retrieval              |

Rule of thumb:
- Use save() or saveAndFlush() for most tests.
- Use clear() only if you want to simulate fetching a detached entity from the database.



Why it matters in tests?
Ex:
BankProduct saved = repository.saveAndFlush(testProduct1);      // If you just do repository.save(x) or saveAndFlush(x), the entity is still managed.
BankProduct found = repository.findById(saved.getId()).get();   // JPA might return the same in-memory instance, not actually querying the database

If you did this instead:
@Autowired
EntityManager entityManager;

BankProduct saved = entityManager.persistAndFlush(testProduct1);
entityManager.clear(); // detach
BankProduct found = repository.findById(saved.getId()).get(); // forced DB query. 'found' is guaranteed to be loaded from the database

 */