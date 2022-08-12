package com.arman.OnlineShop.repository;

import com.arman.OnlineShop.model.OrderDetails;
import com.arman.OnlineShop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByEmail(String email);

}
