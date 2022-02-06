package com.arman.OnlineShop.service;

import com.arman.OnlineShop.model.Product;
import com.arman.OnlineShop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public List<Product> getTop12() {
        return productRepository.findTop12ByOrderByAddingToDesc();
    }

    public List<Product> newProducts() {
        return productRepository.findAllByOrderByAddingToDesc().stream()
                .filter(p -> p.getAddingTo().isAfter(LocalDateTime.now().minusWeeks(1)))
                .collect(Collectors.toList());
    }

    public Product getOne(Long id) {
        return productRepository.getById(id);
    }

    public Product save(Product product) {
        Product productFromDB = productRepository.findById(product.getId()).orElseGet(() -> {
            Product newProduct = new Product();
            newProduct.setName(product.getName());
            newProduct.setDescription(product.getDescription());
            newProduct.setPrice(product.getPrice());
            newProduct.setAmount(product.getAmount());
            newProduct.setPicture(product.getPicture()); // TODO: 06.02.2022 Saving picture
            newProduct.setProperties(product.getProperties());
            newProduct.setAddingTo(LocalDateTime.now());

            return newProduct;
        });

        return productRepository.save(productFromDB);
    }
}
