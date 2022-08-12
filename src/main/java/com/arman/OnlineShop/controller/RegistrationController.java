package com.arman.OnlineShop.controller;

import com.arman.OnlineShop.model.User;
import com.arman.OnlineShop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/registration")
public class RegistrationController {
    private final UserService userService;
    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String register(Model model) {
        model.addAttribute("user", new User());

        return "registration";
    }

    @PostMapping
    public String registerUser(@Valid User user,
                               BindingResult bindingResult,
                               @RequestParam String confirmPassword, Model model) {
        Map<String, String> exist = new HashMap<>();
        if(bindingResult.hasErrors()){
            return "registration";
        }
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("confirmation", "Passwords are not equals");
            return "registration";
        }
        if (!userService.register(user, exist)) {
            if (exist.containsKey("usernameExist"))
                model.addAttribute("usernameExist", "An account already exists for this username");
            if (exist.containsKey("emailExist"))
                model.addAttribute("emailExist", "An account already exists for this email");
            return "registration";
        }

        return "redirect:/login";
    }
}
