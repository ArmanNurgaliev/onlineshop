package com.arman.OnlineShop.model;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class OrderDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @ManyToOne(
            cascade = CascadeType.MERGE
    )
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "user_id"
    )
    private User userId;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    private Product productId;

    private double orderPrice;
    private int orderQuantity;
    private LocalDateTime addingTo;
    private boolean isOrdered;

    public void setQuantity(int n) {
        this.orderQuantity = n;
        this.orderPrice = productId.getPrice() * n;
    }
}
