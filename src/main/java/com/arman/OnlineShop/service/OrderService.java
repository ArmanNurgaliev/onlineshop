package com.arman.OnlineShop.service;

import com.arman.OnlineShop.model.Order;
import com.arman.OnlineShop.model.OrderDetails;
import com.arman.OnlineShop.model.Product;
import com.arman.OnlineShop.model.User;
import com.arman.OnlineShop.repository.OrderDetailsRepository;
import com.arman.OnlineShop.repository.OrderRepository;
import com.arman.OnlineShop.repository.ProductRepository;
import com.arman.OnlineShop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderDetailsRepository orderDetailsRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderService(OrderDetailsRepository orderDetailsRepository, OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.orderDetailsRepository = orderDetailsRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public double getSumOfOrder(User user) {
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAllByUserId(user)
                .stream()
                .filter(orderDetails -> !orderDetails.isOrdered()).collect(Collectors.toList());

        double sum = 0;
        for (OrderDetails orderDetails: orderDetailsList)
            sum += (orderDetails.getOrderPrice() * orderDetails.getOrderQuantity());

        System.out.println("Sum from service: " + sum);
        return sum;
    }


    public Order saveOrder(String orderFullName, String orderPhone, String orderEmail,
                           String orderCountry, String orderZip, String orderCity,
                           String orderShipAddress, User user) {
        List<OrderDetails> details = orderDetailsRepository.findAllByUserId(user).stream().distinct()
                .filter(orderDetails -> !orderDetails.isOrdered()).collect(Collectors.toList());

        System.out.println(details);

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setOrderTrackingNumber(UUID.randomUUID().toString());
        order.setOrderFullName(orderFullName);
        order.setOrderPhone(orderPhone);
        order.setOrderEmail(orderEmail);
        order.setOrderCountry(orderCountry);
        order.setOrderZip(orderZip);
        order.setOrderCity(orderCity);
        order.setOrderShipAddress(orderShipAddress);
        order.setUser(user);


        double sum = 0;
        for (OrderDetails detail: details) {
            Product product = detail.getProductId();
            product.setAmount(product.getAmount()-detail.getOrderQuantity());
            productRepository.save(product);

           // user.removeOrders(detail);
        //    detail.setUserId(null);
            detail.setOrdered(true);
            orderDetailsRepository.save(detail);

            sum += (detail.getOrderPrice() * detail.getOrderQuantity());
        }

      //  userRepository.save(user);
        order.setOrderDetails(details);
        order.setUser(user);
        order.setOrderPrice(sum);
        return orderRepository.save(order);
    }

    public List<Order> findAllOrders(User user) {
        return orderRepository.findAllByUserId(user.getId());
    }

    public Order findOrder(Long orderId) {
        return orderRepository.findById(orderId).get();
    }
}
