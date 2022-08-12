package com.arman.OnlineShop.controller;

import com.arman.OnlineShop.model.Product;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
public class SingleProductController {
    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public SingleProductController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping("/{productId}")
    public String getProduct(@AuthenticationPrincipal User user,
                            Authentication authentication,
                            Model model, @PathVariable Long productId) {
        if (user == null && authentication != null) {
            DefaultOidcUser oAuth2User = (DefaultOidcUser) authentication.getPrincipal();
            user = userService.getUserByEmail(oAuth2User.getEmail());
        }

        Product product = productService.getOne(productId);
        model.addAttribute("user", user);
        model.addAttribute("product", product);
        model.addAttribute("containsOrderDetails", userService.checkOrderDetails(user, product));
        model.addAttribute("numProductsInCart", userService.getNumProductsInCart(user));

        return "product-page";
    }

    @GetMapping("/add-to-cart/{productId}")
    public String addProductToCart(@AuthenticationPrincipal User user,
                                   Authentication authentication,
                                   @PathVariable Long productId) {
        if (user == null && authentication != null) {
            DefaultOidcUser oAuth2User = (DefaultOidcUser) authentication.getPrincipal();
            user = userService.getUserByEmail(oAuth2User.getEmail());
        }

        userService.addProductToCart(productId, user);

        return "redirect:/product/" + productId;
    }
}
