package com.arman.OnlineShop.controller;

import com.arman.OnlineShop.model.Product;
import com.arman.OnlineShop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
public class SingleProductController {
    private final ProductService productService;

    @Autowired
    public SingleProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public String getProduct(Model model, @PathVariable Long productId) {
        Product product = productService.getOne(productId);
        model.addAttribute("product", product);
        model.addAttribute("properties", product.getProperties());

        return "product-page";
    }
}
