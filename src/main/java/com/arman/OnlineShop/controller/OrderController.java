package com.arman.OnlineShop.controller;

import com.arman.OnlineShop.model.Order;
import com.arman.OnlineShop.model.OrderDetails;
import com.arman.OnlineShop.model.User;
import com.arman.OnlineShop.service.OrderService;
import com.arman.OnlineShop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Value("${STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;
    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public OrderController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String orderDetails(@AuthenticationPrincipal User user,
                               Authentication authentication, Model model) {
        if (user == null && authentication != null) {
            DefaultOidcUser oAuth2User = (DefaultOidcUser) authentication.getPrincipal();
            user = userService.getUserByEmail(oAuth2User.getEmail());
        }

        if (userService.getNumProductsInCart(user) == 0)
            return "redirect:/cart/" + user.getId();

        double sumOfOrder = orderService.getSumOfOrder(user);
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("sum", sumOfOrder);
        model.addAttribute("order", new Order());
        model.addAttribute("user", user);
        model.addAttribute("numProductsInCart", userService.getNumProductsInCart(user));

        return "shipment-form";
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String ordersList(@PathVariable Long userId, Model model) {
        User user = userService.getUserById(userId);

        model.addAttribute("user", user);
        model.addAttribute("orders", orderService.findAllOrders(user));
        model.addAttribute("numProductsInCart", userService.getNumProductsInCart(user));

        return "order-list";
    }

    @PostMapping("/charge")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String successBuy(@AuthenticationPrincipal User user,
                             Authentication authentication,
                             @RequestParam(value = "orderFullName") String orderFullName,
                             @RequestParam(value = "orderPhone") String orderPhone,
                             @RequestParam(value = "orderEmail") String orderEmail,
                             @RequestParam(value = "orderCountry") String orderCountry,
                             @RequestParam(value = "orderZip") String orderZip,
                             @RequestParam(value = "orderCity") String orderCity,
                             @RequestParam(value = "orderShipAddress") String orderShipAddress,
                             Model model) {
        if (user == null && authentication != null) {
            DefaultOidcUser oAuth2User = (DefaultOidcUser) authentication.getPrincipal();
            user = userService.getUserByEmail(oAuth2User.getEmail());
        }

        Order savedOrder = orderService.saveOrder(orderFullName, orderPhone, orderEmail, orderCountry, orderZip,
                orderCity, orderShipAddress, user);


        return "redirect:/order/" + user.getId();
    }

    @GetMapping("/details/{orderId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String order(@AuthenticationPrincipal User user,
                        Authentication authentication,
                        @PathVariable Long orderId, Model model) {
        if (user == null && authentication != null) {
            DefaultOidcUser oAuth2User = (DefaultOidcUser) authentication.getPrincipal();
            user = userService.getUserByEmail(oAuth2User.getEmail());
        }

        Order order = orderService.findOrder(orderId);
        List<OrderDetails> orderDetailsList = order.getOrderDetails();
        double sum = 0;
        for (OrderDetails details: orderDetailsList)
            sum += (details.getOrderQuantity() * details.getOrderPrice());

        model.addAttribute("orderDetails", orderDetailsList);
        model.addAttribute("user", user);
        model.addAttribute("sum", sum);
        model.addAttribute("numProductsInCart", userService.getNumProductsInCart(user));

        return "order-details";
    }
}
