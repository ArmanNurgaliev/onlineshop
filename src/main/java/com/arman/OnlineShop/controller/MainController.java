package com.arman.OnlineShop.controller;

import com.arman.OnlineShop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    private final ProductService productService;

    @Autowired
    public MainController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String main(Model model) {
        model.addAttribute("products", productService.getTop12());

        return "index";
    }
}
