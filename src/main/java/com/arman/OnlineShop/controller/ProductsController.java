package com.arman.OnlineShop.controller;

import com.arman.OnlineShop.model.Product;
import com.arman.OnlineShop.model.User;
import com.arman.OnlineShop.service.ProductService;
import com.arman.OnlineShop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductsController {
    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public ProductsController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping("/all")
    public String products(@AuthenticationPrincipal User user,
                           Authentication authentication,
                           Model model) {
        if (user == null && authentication != null) {
            DefaultOidcUser oAuth2User = (DefaultOidcUser) authentication.getPrincipal();
            user = userService.getUserByEmail(oAuth2User.getEmail());
        }
            return productsPaginated(1, user, model);
    }

    @GetMapping("/new")
    public String newProducts(@AuthenticationPrincipal User user,
                              Authentication authentication,
                              Model model) {
        if (user == null && authentication != null) {
            DefaultOidcUser oAuth2User = (DefaultOidcUser) authentication.getPrincipal();
            user = userService.getUserByEmail(oAuth2User.getEmail());
        }

        model.addAttribute("user", user);
        model.addAttribute("products", productService.newProducts());
        model.addAttribute("numProductsInCart", userService.getNumProductsInCart(user));

        return "products";
    }

    @GetMapping("/{pageNumber}")
    public String productsPaginated(@PathVariable int pageNumber, User user, Model model) {
        int pageSize = 12;
        Page<Product> productPage = productService.getPaginatedProducts(pageNumber, pageSize);
        List<Product> products = productPage.getContent();

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("products", products);
        model.addAttribute("user", user);
        model.addAttribute("numProductsInCart", userService.getNumProductsInCart(user));

        return "products";
    }

    @GetMapping("/add/new")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String addProduct(@AuthenticationPrincipal User user,
                             Authentication authentication,
                             Model model) {
        if (user == null && authentication != null) {
            DefaultOidcUser oAuth2User = (DefaultOidcUser) authentication.getPrincipal();
            user = userService.getUserByEmail(oAuth2User.getEmail());
        }
        Product product = new Product();

        model.addAttribute("product", product);
        model.addAttribute("numProductsInCart", userService.getNumProductsInCart(user));
        model.addAttribute("user", user);

        return "add-product";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String addProduct(@Valid @ModelAttribute Product product,
                             BindingResult bindingResult,
                             @RequestParam("file") MultipartFile file) {
        if (bindingResult.hasErrors())
            return "add-product";

        productService.save(product, file);
        return "redirect:/products/all";
    }
}
