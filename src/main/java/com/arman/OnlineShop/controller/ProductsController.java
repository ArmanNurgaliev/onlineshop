package com.arman.OnlineShop.controller;

import com.arman.OnlineShop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/products")
public class ProductsController {
    private final ProductService productService;

    @Autowired
    public ProductsController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/all")
    public String products(Model model) {

        model.addAttribute("products", productService.getAll());
        return "products";
    }

    @GetMapping("/new")
    public String newProducts(Model model) {
        model.addAttribute("products", productService.newProducts());

        return "products";
    }
}
