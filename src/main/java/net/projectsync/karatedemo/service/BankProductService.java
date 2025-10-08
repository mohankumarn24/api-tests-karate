package net.projectsync.karatedemo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import net.projectsync.karatedemo.model.BankProduct;
import net.projectsync.karatedemo.repository.BankProductRepository;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BankProductService {

    private final BankProductRepository repository;

    // CREATE
    public BankProduct createProduct(BankProduct product) {
        return repository.save(product);
    }

    // READ (by ID)
    public Optional<BankProduct> getProductById(Long id) {
        return repository.findById(id);
    }

    // READ (all)
    public List<BankProduct> getAllProducts() {
        return repository.findAll();
    }

    // UPDATE
    public Optional<BankProduct> updateProduct(Long id, BankProduct updatedProduct) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setTitle(updatedProduct.getTitle());
                    return repository.save(existing);
                });
    }

    // DELETE (by ID)
    public boolean deleteProduct(Long id) {
        Optional<BankProduct> productOptional = repository.findById(id);
        if (productOptional.isPresent()) {
            repository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
