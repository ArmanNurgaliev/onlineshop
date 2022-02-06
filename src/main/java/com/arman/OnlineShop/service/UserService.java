package com.arman.OnlineShop.service;

import com.arman.OnlineShop.model.User;
import com.arman.OnlineShop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user != null) return user;
        throw new UsernameNotFoundException("No user with username " + username);
    }

    public User save(User user) {
        User userFromDB = userRepository.findByUsername(user.getUsername());

        return userRepository.save(userFromDB);
    }
}
