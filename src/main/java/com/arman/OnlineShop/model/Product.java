package com.arman.OnlineShop.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double price;
    private String description;
    private Long amount;
    private LocalDateTime addingTo;
    private String picture;

    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name = "product_properties", joinColumns = @JoinColumn(name = "id"))
    @MapKeyColumn(name="property")
    @Column(name = "value")
    private Map<String, String> properties = new HashMap<>();
}
