package net.projectsync.karatedemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.projectsync.karatedemo.model.BankProduct;
import net.projectsync.karatedemo.service.BankProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BankProductController.class)
class BankProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BankProductService service;

    // ----------------------------
    // CREATE: POST /bankproducts
    // ----------------------------
    @Test
    void testCreateProduct() throws Exception {
        BankProduct request = new BankProduct("Savings Account");
        BankProduct saved = new BankProduct("Savings Account");
        saved.setId(1L);

        Mockito.when(service.createProduct(any(BankProduct.class))).thenReturn(saved);

        mockMvc.perform(post("/bankproducts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/bankproducts/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Savings Account"));
    }

    // ----------------------------
    // READ: GET /bankproducts/{id} (found)
    // ----------------------------
    @Test
    void testGetProductByIdFound() throws Exception {
        BankProduct product = new BankProduct("Savings Account");
        product.setId(1L);

        Mockito.when(service.getProductById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/bankproducts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Savings Account"));
    }

    // ----------------------------
    // READ: GET /bankproducts/{id} (not found)
    // ----------------------------
    @Test
    void testGetProductByIdNotFound() throws Exception {
        Mockito.when(service.getProductById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/bankproducts/99"))
                .andExpect(status().isNotFound());
    }

    // ----------------------------
    // READ: GET /bankproducts (all products)
    // ----------------------------
    @Test
    void testGetAllProducts() throws Exception {
        BankProduct p1 = new BankProduct("Savings Account");
        p1.setId(1L);
        BankProduct p2 = new BankProduct("Fixed Deposit");
        p2.setId(2L);

        Mockito.when(service.getAllProducts()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/bankproducts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Savings Account"))
                .andExpect(jsonPath("$[1].title").value("Fixed Deposit"));
    }

    // ----------------------------
    // UPDATE: PUT /bankproducts/{id} (found)
    // ----------------------------
    @Test
    void testUpdateProductFound() throws Exception {
        BankProduct updated = new BankProduct("New Title");
        updated.setId(1L);

        Mockito.when(service.updateProduct(eq(1L), any(BankProduct.class)))
                .thenReturn(Optional.of(updated));

        mockMvc.perform(put("/bankproducts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Title"));
    }

    // ----------------------------
    // UPDATE: PUT /bankproducts/{id} (not found)
    // ----------------------------
    @Test
    void testUpdateProductNotFound() throws Exception {
        BankProduct updated = new BankProduct("New Title");
        updated.setId(99L);

        Mockito.when(service.updateProduct(eq(99L), any(BankProduct.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/bankproducts/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());
    }

    // ----------------------------
    // DELETE: DELETE /bankproducts/{id} (found)
    // ----------------------------
    @Test
    void testDeleteProductFound() throws Exception {
        Mockito.when(service.deleteProduct(1L)).thenReturn(true);

        mockMvc.perform(delete("/bankproducts/1"))
                .andExpect(status().isNoContent());
    }

    // ----------------------------
    // DELETE: DELETE /bankproducts/{id} (not found)
    // ----------------------------
    @Test
    void testDeleteProductNotFound() throws Exception {
        Mockito.when(service.deleteProduct(99L)).thenReturn(false);

        mockMvc.perform(delete("/bankproducts/99"))
                .andExpect(status().isNotFound());
    }
}
