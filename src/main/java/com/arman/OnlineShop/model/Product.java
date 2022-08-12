package com.arman.OnlineShop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Name can not be empty")
    private String name;
    @Min(0L)
    private double price;
    private String description;
    @Min(1)
    private Long amount;
    private LocalDateTime addingTo;
    private String picture;


    @OneToMany(mappedBy = "productId",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetails> orderDetails = new ArrayList<>();

    public void addOrderDetails(OrderDetails orderDetail) {
        this.orderDetails.add(orderDetail);
        orderDetail.setProductId(this);
    }

    public void removeOrderDetails(OrderDetails orderDetail) {
        this.orderDetails.remove(orderDetail);
        orderDetail.setProductId(null);
    }
}
