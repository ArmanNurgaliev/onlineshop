package com.arman.OnlineShop.config;

import com.arman.OnlineShop.model.AuthenticationProvider;
import com.arman.OnlineShop.model.Role;
import com.arman.OnlineShop.model.User;
import com.arman.OnlineShop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CommandLineAppStartupRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@email.ru") != null)
            return;

        User admin =
                User.builder()
                        .username("admin")
                        .email("admin@email.ru")
                        .password(passwordEncoder.encode("admin"))
                        .enabled(true)
                        .authProvider(AuthenticationProvider.LOCAL)
                        .build();
        admin.setRoles(Set.of(Role.ROLE_USER, Role.ROLE_ADMIN));

        userRepository.save(admin);
    }
}
