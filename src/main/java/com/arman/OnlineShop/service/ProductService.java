package com.arman.OnlineShop.service;

import com.arman.OnlineShop.model.Product;
import com.arman.OnlineShop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Value("${upload.path}")
    private String uploadPath;
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getTop12() {
        return productRepository.findTop12ByOrderByIdDesc();
    }

    public List<Product> newProducts() {
        return productRepository.findAllByOrderByAddingToDesc().stream()
                .filter(p -> p.getAddingTo().isAfter(LocalDateTime.now().minusWeeks(1)))
                .collect(Collectors.toList());
    }

    public Product getOne(Long id) {
        return productRepository.getById(id);
    }

    public Product save(Product product, MultipartFile file) {
        Product productFromDB = productRepository.findByName(product.getName()).orElseGet(() -> {
            Product newProduct = new Product();
            newProduct.setName(product.getName());
            newProduct.setDescription(product.getDescription());
            newProduct.setPrice(product.getPrice());
            newProduct.setAmount(product.getAmount());

      //      newProduct.setProperties(propertyDto.getProperties());

            newProduct.setAddingTo(LocalDateTime.now());

            try {
                saveProductImage(newProduct, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newProduct;
        });

        return productRepository.save(productFromDB);
    }

    private void saveProductImage(Product product, MultipartFile file) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists())
                uploadDir.mkdir();
            String uuidFile = UUID.randomUUID().toString();
            String resultName = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultName));
            product.setPicture("http://127.0.0.1:8887//" + resultName);
        }
    }

    public Page<Product> getPaginatedProducts(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
        return productRepository.findAll(pageable);
    }
}
