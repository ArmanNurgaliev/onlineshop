package com.arman.OnlineShop.repository;

import com.arman.OnlineShop.model.OrderDetails;
import com.arman.OnlineShop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
    List<OrderDetails> findAllByUserId(User user);
    List<OrderDetails> findAllByUserIdOrderByAddingToDesc(User user);

    void deleteById(Long order_details_id);
}
