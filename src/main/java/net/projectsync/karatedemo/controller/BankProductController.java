package net.projectsync.karatedemo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import net.projectsync.karatedemo.model.BankProduct;
import net.projectsync.karatedemo.service.BankProductService;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/bankproducts")
@RequiredArgsConstructor
public class BankProductController {

    private final BankProductService bankProductService; // Constructor injection via Lombok

    // ----------------------------
    // CREATE
    // ----------------------------
    @PostMapping
    public ResponseEntity<BankProduct> createProduct(@Valid @RequestBody BankProduct product) {
        BankProduct created = bankProductService.createProduct(product);
        return ResponseEntity
                .created(URI.create("/bankproducts/" + created.getId())) // Location header
                .body(created);
    }

    // ----------------------------
    // READ (by ID)
    // ----------------------------
    @GetMapping("/{id}")
    public ResponseEntity<BankProduct> getProductById(@PathVariable Long id) {
        return bankProductService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------------------
    // READ (all products)
    // ----------------------------
    @GetMapping
    public ResponseEntity<List<BankProduct>> getAllProducts() {
        List<BankProduct> products = bankProductService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // ----------------------------
    // UPDATE
    // ----------------------------
    @PutMapping("/{id}")
    public ResponseEntity<BankProduct> updateProduct(@PathVariable Long id,
                                                     @Valid @RequestBody BankProduct product) {
        return bankProductService.updateProduct(id, product)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------------------
    // DELETE (by ID)
    // ----------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = bankProductService.deleteProduct(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
