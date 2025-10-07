package ru.igorivkin.karatedemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.igorivkin.karatedemo.model.BankProduct;

public interface BankProductRepository extends JpaRepository<BankProduct, Long> {

    // JpaRepository already provides standard CRUD methods:
    // save(), findById(), findAll(), deleteById(), etc.
}
