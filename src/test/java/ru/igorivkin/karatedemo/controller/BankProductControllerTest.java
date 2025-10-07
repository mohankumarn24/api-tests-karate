package ru.igorivkin.karatedemo.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.igorivkin.karatedemo.model.BankProduct;
import ru.igorivkin.karatedemo.service.BankProductService;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class BankProductControllerUnitTest {

    @Mock
    private BankProductService service;

    @InjectMocks
    private BankProductController controller;

    // ----------------------------
    // CREATE
    // ----------------------------
    @Test
    void testCreateProduct() {
        BankProduct product = new BankProduct("New Product");
        BankProduct created = new BankProduct("New Product");
        created.setId(1L);

        when(service.createProduct(product)).thenReturn(created);

        ResponseEntity<BankProduct> response = controller.createProduct(product);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(created, response.getBody());
        verify(service).createProduct(product);
    }

    // ----------------------------
    // READ BY ID
    // ----------------------------
    @Test
    void testGetProductByIdFound() {
        BankProduct product = new BankProduct("Test");
        product.setId(1L);
        when(service.getProductById(1L)).thenReturn(Optional.of(product));

        ResponseEntity<BankProduct> response = controller.getProductById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(product, response.getBody());
    }

    @Test
    void testGetProductByIdNotFound() {
        when(service.getProductById(1L)).thenReturn(Optional.empty());

        ResponseEntity<BankProduct> response = controller.getProductById(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
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

        List<BankProduct> products = Arrays.asList(p1, p2);
        when(service.getAllProducts()).thenReturn(products);

        ResponseEntity<List<BankProduct>> response = controller.getAllProducts();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals(products, response.getBody());
    }

    // ----------------------------
    // UPDATE
    // ----------------------------
    @Test
    void testUpdateProductFound() {
        BankProduct updated = new BankProduct("Updated");
        updated.setId(1L);

        when(service.updateProduct(eq(1L), any(BankProduct.class))).thenReturn(Optional.of(updated));

        ResponseEntity<BankProduct> response = controller.updateProduct(1L, updated);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updated, response.getBody());
        verify(service).updateProduct(1L, updated);
    }

    @Test
    void testUpdateProductNotFound() {
        BankProduct updated = new BankProduct("Updated");

        when(service.updateProduct(eq(1L), any(BankProduct.class))).thenReturn(Optional.empty());

        ResponseEntity<BankProduct> response = controller.updateProduct(1L, updated);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    // ----------------------------
    // DELETE
    // ----------------------------
    @Test
    void testDeleteProductFound() {
        when(service.deleteProduct(1L)).thenReturn(true);

        ResponseEntity<Void> response = controller.deleteProduct(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(service).deleteProduct(1L);
    }

    @Test
    void testDeleteProductNotFound() {
        when(service.deleteProduct(1L)).thenReturn(false);

        ResponseEntity<Void> response = controller.deleteProduct(1L);

        assertEquals(404, response.getStatusCodeValue());
        verify(service).deleteProduct(1L);
    }
}

