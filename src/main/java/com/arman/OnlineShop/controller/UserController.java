package com.arman.OnlineShop.controller;

import com.arman.OnlineShop.model.Role;
import com.arman.OnlineShop.model.User;
import com.arman.OnlineShop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String users(@AuthenticationPrincipal User user,
                        Authentication authentication,
                        Model model) {
        if (user == null && authentication != null) {
            DefaultOidcUser oAuth2User = (DefaultOidcUser) authentication.getPrincipal();
            user = userService.getUserByEmail(oAuth2User.getEmail());
        }

        return usersPaginated(1, model, user);
    }

    @GetMapping("{pageNumber}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String usersPaginated(@PathVariable(value = "pageNumber") int pageNumber,
                                 Model model, User user) {
        int pageSize = 5;
        Page<User> page = userService.getPaginatedUsers(pageNumber, pageSize);
        List<User> userList = page.getContent();
        List<Role> roles = List.of(Role.values());

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("users", userList);
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        model.addAttribute("numProductsInCart", userService.getNumProductsInCart(user));

        return "users";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String saveUser(@RequestParam("roles") Set<Role> roles,
                           @RequestParam("userId") Long id) {
        userService.saveRoles(id, roles);

        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        return "redirect:/users";
    }
}
