package com.arman.OnlineShop.service;

import com.arman.OnlineShop.dto.OrderDetailsDto;
import com.arman.OnlineShop.model.OrderDetails;
import com.arman.OnlineShop.model.User;
import com.arman.OnlineShop.repository.OrderDetailsRepository;
import com.arman.OnlineShop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {
    private final UserRepository userRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final UserService userService;

    @Autowired
    public CartService(UserRepository userRepository, OrderDetailsRepository orderDetailsRepository, UserService userService) {
        this.userRepository = userRepository;
        this.orderDetailsRepository = orderDetailsRepository;
        this.userService = userService;
    }

    public Double getSum(List<OrderDetails> orders) {
        return orders.stream().mapToDouble(p -> p.getOrderQuantity() * p.getProductId().getPrice()).sum();
    }

    public List<OrderDetails> getOrderDetails(Long userId) {
        return orderDetailsRepository.findAllByUserIdOrderByAddingToDesc(userRepository.findById(userId).get());
    }


    public Long deleteItem(Long order_details_id, User user) {
        /*OrderDetails orderDetails = orderDetailsRepository.findById(order_details_id).get();
        orderDetailsRepository.deleteById(order_details_id);
    //    user.removeOrders(orderDetails);
        userRepository.save(user);*/
        orderDetailsRepository.deleteById(order_details_id);

        return userService.getNumProductsInCart(user);
    }


    public OrderDetails minusAmount(Long order_details_id) {
        OrderDetails orderDetailsFromDB = orderDetailsRepository.findById(order_details_id).get();
        int quantity = orderDetailsFromDB.getOrderQuantity();
        if (quantity == 1)
            return orderDetailsFromDB;

        orderDetailsFromDB.setOrderQuantity(quantity-1);
        return orderDetailsRepository.save(orderDetailsFromDB);
    }

    public OrderDetails plusAmount(Long order_details_id) {
        OrderDetails orderDetailsFromDB = orderDetailsRepository.findById(order_details_id).get();
        int quantity = orderDetailsFromDB.getOrderQuantity();
        if (quantity == orderDetailsFromDB.getProductId().getAmount())
            return orderDetailsFromDB;
        orderDetailsFromDB.setOrderQuantity(quantity+1);
        return orderDetailsRepository.save(orderDetailsFromDB);

    }

    public List<OrderDetails> getOrderDetailsFromDB(User user) {
        return orderDetailsRepository.findAllByUserId(user);
    }

    public List<OrderDetailsDto> getOrderDetailsDto(User user) {
        List<OrderDetails> orderDetails =
                this.getOrderDetailsFromDB(user).stream().distinct()
                .filter(o -> !o.isOrdered())
                        .sorted((o1, o2) -> (int) (o1.getId()-o2.getId()))
                        .collect(Collectors.toList());

        List<OrderDetailsDto> orderDetailsDto = new ArrayList<>();
        for (OrderDetails orderDetail: orderDetails) {
            OrderDetailsDto order = new OrderDetailsDto();
            order.setOrder_details_id(orderDetail.getId());
            order.setAddingTo(orderDetail.getAddingTo());
            order.setOrderPrice(orderDetail.getOrderPrice());
            order.setOrderQuantity(orderDetail.getOrderQuantity());
            order.setUserId(user.getId());
            order.setProductId(orderDetail.getProductId().getId());
            order.setProductImage(orderDetail.getProductId().getPicture());
            order.setProductName(orderDetail.getProductId().getName());
            order.setProductPrice(orderDetail.getProductId().getPrice());
            order.setProductAmount(orderDetail.getProductId().getAmount());

            orderDetailsDto.add(order);
        }
        return orderDetailsDto;
    }
}
