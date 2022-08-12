package com.arman.OnlineShop.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Data
public class OrderDetailsDto {
    private Long order_details_id;
    private Long userId;
    private Long productId;
    private String productImage;
    private String productName;
    private double productPrice;
    private Long productAmount;

    private double orderPrice;
    private int orderQuantity;
    private LocalDateTime addingTo;
}
