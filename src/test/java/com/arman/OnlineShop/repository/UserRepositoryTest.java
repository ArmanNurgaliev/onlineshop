package com.arman.OnlineShop.repository;

import com.arman.OnlineShop.model.Role;
import com.arman.OnlineShop.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void addUser() {
        User user =
                User.builder()
                        .username("TestUser")
                        .email("test@gmail.com")
                        .password("123")
                        .roles(Collections.singleton(Role.ROLE_USER))
                        .build();

        userRepository.save(user);
    }
}