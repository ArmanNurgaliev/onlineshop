package com.arman.OnlineShop.controller;

import com.arman.OnlineShop.model.User;
import com.arman.OnlineShop.service.ProductService;
import com.arman.OnlineShop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public MainController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping
    public String main(@AuthenticationPrincipal User user,
                       Authentication authentication,
                       Model model) {
        if (user == null && authentication != null) {
            DefaultOidcUser oAuth2User = (DefaultOidcUser) authentication.getPrincipal();
            user = userService.getUserByEmail(oAuth2User.getEmail());
        }

        model.addAttribute("user", user);
        model.addAttribute("products", productService.getTop12());
        model.addAttribute("numProductsInCart", userService.getNumProductsInCart(user));

        return "index";
    }
}
