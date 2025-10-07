package ru.igorivkin.karatedemo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.igorivkin.karatedemo.model.BankProduct;
import ru.igorivkin.karatedemo.repository.BankProductRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankProductServiceTest {

    @Mock
    private BankProductRepository repository;

    @InjectMocks
    private BankProductService service;

    // ----------------------------
    // CREATE
    // ----------------------------
    @Test
    void testCreateProduct() {
        BankProduct product = new BankProduct("New Product");
        BankProduct saved = new BankProduct("New Product");
        saved.setId(1L);

        when(repository.save(product)).thenReturn(saved);

        BankProduct result = service.createProduct(product);

        assertEquals(saved, result);
        verify(repository).save(product);
    }

    // ----------------------------
    // READ BY ID
    // ----------------------------
    @Test
    void testGetProductByIdFound() {
        BankProduct product = new BankProduct("Test");
        product.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(product));

        Optional<BankProduct> result = service.getProductById(1L);

        assertTrue(result.isPresent());
        assertEquals(product, result.get());
    }

    @Test
    void testGetProductByIdNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Optional<BankProduct> result = service.getProductById(1L);

        assertFalse(result.isPresent());
    }

    // ----------------------------
    // READ ALL
    // ----------------------------
    @Test
    void testGetAllProducts() {
        BankProduct p1 = new BankProduct("A");
        p1.setId(1L);
        BankProduct p2 = new BankProduct("B");
        p2.setId(2L);

        when(repository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<BankProduct> result = service.getAllProducts();

        assertEquals(2, result.size());
        assertEquals(Arrays.asList(p1, p2), result);
    }

    // ----------------------------
    // UPDATE
    // ----------------------------
    @Test
    void testUpdateProductFound() {
        BankProduct existing = new BankProduct("Old");
        existing.setId(1L);
        BankProduct updated = new BankProduct("New");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(new BankProduct("New") {{ setId(1L); }});

        Optional<BankProduct> result = service.updateProduct(1L, updated);

        assertTrue(result.isPresent());
        assertEquals("New", result.get().getTitle());
        verify(repository).findById(1L);
        verify(repository).save(existing);
    }

    @Test
    void testUpdateProductNotFound() {
        BankProduct updated = new BankProduct("New");

        when(repository.findById(1L)).thenReturn(Optional.empty());

        Optional<BankProduct> result = service.updateProduct(1L, updated);

        assertFalse(result.isPresent());
        verify(repository).findById(1L);
        verify(repository, never()).save(any());
    }

    // ----------------------------
    // DELETE
    // ----------------------------
    @Test
    void testDeleteProductFound() {
        BankProduct product = new BankProduct("ToDelete");
        product.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(repository).deleteById(1L);

        boolean deleted = service.deleteProduct(1L);

        assertTrue(deleted);
        verify(repository).findById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void testDeleteProductNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        boolean deleted = service.deleteProduct(1L);

        assertFalse(deleted);
        verify(repository).findById(1L);
        verify(repository, never()).deleteById(anyLong());
    }
}

