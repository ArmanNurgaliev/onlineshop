package com.arman.OnlineShop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            cascade = CascadeType.MERGE
    )
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "user_id"
    )
    @ToString.Exclude
    private User user;

    private String orderFullName;
    private String orderPhone;
    private String orderEmail;
    private String orderCountry;
    private String orderCity;
    private String orderShipAddress;
    private String orderZip;
    private LocalDateTime orderDate;
    private boolean isOrderShipped;
    private String orderTrackingNumber;
    private double orderPrice;

    @ManyToMany(
            cascade = CascadeType.ALL
    )
    @JoinTable(
            name = "orders_to_order_details_map",
            joinColumns = @JoinColumn(
                    name = "order_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "order_details_id",
                    referencedColumnName = "id"
            )
    )
    @ToString.Exclude
    private List<OrderDetails> orderDetails;


    public void addOrderDetails(OrderDetails orderDetail) {
        this.orderDetails.add(orderDetail);
    }

    public void removeOrderDetails(OrderDetails orderDetail) {
        this.orderDetails.remove(orderDetail);
    }

}
