package com.arman.OnlineShop.controller;

import com.arman.OnlineShop.dto.OrderDetailsDto;
import com.arman.OnlineShop.model.OrderDetails;
import com.arman.OnlineShop.model.User;
import com.arman.OnlineShop.service.CartService;
import com.arman.OnlineShop.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
@Slf4j
public class CartController {
    @Value("${STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;

    private final UserService userService;
    private final CartService cartService;

    @Autowired
    public CartController(UserService userService, CartService cartService) {
        this.userService = userService;
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String userCard(@AuthenticationPrincipal User user,
                           Authentication authentication,
                           @PathVariable("userId") Long id, Model model) {
        if (user == null && authentication != null) {
            DefaultOidcUser oAuth2User = (DefaultOidcUser) authentication.getPrincipal();
            user = userService.getUserByEmail(oAuth2User.getEmail());
        }

        model.addAttribute("user", user);
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("numProductsInCart", userService.getNumProductsInCart(user));

        return "user-cart";
    }

    @GetMapping("")
    @ResponseBody
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public List<OrderDetailsDto> getCart(@AuthenticationPrincipal User user,
                                      Authentication authentication, Model model) {
        if (user == null && authentication != null) {
            DefaultOidcUser oAuth2User = (DefaultOidcUser) authentication.getPrincipal();
            user = userService.getUserByEmail(oAuth2User.getEmail());
        }

        List<OrderDetailsDto> orderDetailsDto = cartService.getOrderDetailsDto(user);

        return orderDetailsDto;
    }


    @GetMapping("/delete")
    @ResponseBody
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public Long deleteItem(@AuthenticationPrincipal User user,
                           Authentication authentication,
                           @RequestParam Long order_details_id) {
        if (user == null && authentication != null) {
            DefaultOidcUser oAuth2User = (DefaultOidcUser) authentication.getPrincipal();
            user = userService.getUserByEmail(oAuth2User.getEmail());
        }

        return cartService.deleteItem(order_details_id, user);
    }

    @GetMapping("/num-of-products")
    @ResponseBody
    public Long numOfProducts(@AuthenticationPrincipal User user,
                             Authentication authentication) {
        return userService.getNumProductsInCart(user);
    }

    @GetMapping("/plusAmount")
    @ResponseBody
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public Long plusAmount(@RequestParam Long order_details_id) {

        cartService.plusAmount(order_details_id);
        return order_details_id;

    }

    @GetMapping("/minusAmount")
    @ResponseBody
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public Long minusAmount(@RequestParam Long order_details_id) {


        cartService.minusAmount(order_details_id);
        return order_details_id;
    }
}
