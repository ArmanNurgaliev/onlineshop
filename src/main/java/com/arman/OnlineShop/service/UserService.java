package com.arman.OnlineShop.service;

import com.arman.OnlineShop.model.*;
import com.arman.OnlineShop.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.type.LocalDateType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;

    @Autowired
    public UserService(UserRepository userRepository, ProductRepository productRepository, PasswordEncoder passwordEncoder, OrderRepository orderRepository, OrderDetailsRepository orderDetailsRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
        this.orderRepository = orderRepository;
        this.orderDetailsRepository = orderDetailsRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user != null) return user;
        throw new UsernameNotFoundException("No user with email " + email);
    }

    public boolean register(User user, Map<String, String> exist) {
        boolean emailExists = userRepository.findByEmail(user.getEmail()) != null;
        boolean userExists = userRepository.findByUsername(user.getUsername()) != null;
        if (emailExists || userExists) {
            if (userExists)
                exist.put("usernameExist", user.getUsername());
            if (emailExists)
                exist.put("emailExist", user.getEmail());
            return false;
        }
        log.info(String.valueOf(user));
        User newUser = new User();
        BeanUtils.copyProperties(user, newUser);
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setEnabled(true);
        newUser.setAuthProvider(AuthenticationProvider.LOCAL);

        newUser.setRoles(Collections.singleton(Role.ROLE_USER));
        userRepository.save(newUser);
        return true;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void createNewUserAfterOauth2Login(String email, String username, AuthenticationProvider provider) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);

        user.setRoles(Collections.singleton(Role.ROLE_USER));
        user.setEnabled(true);
        user.setAuthProvider(provider);
        user.setPassword(passwordEncoder.encode(String.valueOf(UUID.randomUUID())));
        userRepository.save(user);
    }

    public void updateUserAfterOAuth2Login(User userFromDB, String username, AuthenticationProvider provider) {
        userFromDB.setUsername(username);
        userFromDB.setAuthProvider(provider);

        userRepository.save(userFromDB);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    public void saveRoles(Long id, Set<Role> roles) {
        User user = userRepository.findById(id).get();
        user.getRoles().clear();
        user.setRoles(roles);
        userRepository.save(user);
        System.out.println(user);
    }

    public void deleteUser(Long id) {
        orderRepository.findAllByUserId(id).forEach(o -> o.setUser(null));
        orderDetailsRepository.findAllByUserId(userRepository.findById(id).get()).forEach(o -> o.setUserId(null));

        userRepository.deleteById(id);
    }

    public Page<User> getPaginatedUsers(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber-1, pageSize);

        return userRepository.findAll(pageable);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).get();
    }

    public User getUserByName(String name) {
        return userRepository.findByUsername(name);
    }

    public void addProductToCart(Long productId, User user) {
        Product product = productRepository.findById(productId).get();

        OrderDetails orderDetails = new OrderDetails();

        product.addOrderDetails(orderDetails);
        orderDetails.setQuantity(1);
        orderDetails.setAddingTo(LocalDateTime.now());
        orderDetails.setUserId(user);

        orderDetailsRepository.save(orderDetails);
    }

    public boolean checkOrderDetails(User user, Product product) {
        return orderDetailsRepository.findAllByUserId(user).stream()
                .filter(orderDetails -> !orderDetails.isOrdered())
                .anyMatch(orderDetails -> orderDetails.getProductId().equals(product));

    }

    public Long getNumProductsInCart(User user) {
        return orderDetailsRepository.findAllByUserId(user).stream()
                .filter(orderDetails -> !orderDetails.isOrdered()).count();
    }
}
