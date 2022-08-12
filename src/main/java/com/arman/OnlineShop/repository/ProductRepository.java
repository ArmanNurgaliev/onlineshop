package com.arman.OnlineShop.repository;

import com.arman.OnlineShop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findTop12ByOrderByIdDesc();
    List<Product> findAllByOrderByAddingToDesc();

    Optional<Product> findByName(String name);
}
