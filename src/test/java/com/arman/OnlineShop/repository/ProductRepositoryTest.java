package com.arman.OnlineShop.repository;

import com.arman.OnlineShop.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void addProducts() {
        Product product =
                Product.builder()
                        .name("Iphone 11")
                        .price(1200)
                        .description("Iphone 11, 128 Gb, Black")
                        .amount(5L)
                        .addingTo(LocalDateTime.now())
                        .build();
        productRepository.save(product);
    }
}