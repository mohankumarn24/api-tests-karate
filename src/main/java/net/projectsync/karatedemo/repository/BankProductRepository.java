package net.projectsync.karatedemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import net.projectsync.karatedemo.model.BankProduct;

public interface BankProductRepository extends JpaRepository<BankProduct, Long> {

    // JpaRepository already provides standard CRUD methods:
    // save(), findById(), findAll(), deleteById(), etc.
}
