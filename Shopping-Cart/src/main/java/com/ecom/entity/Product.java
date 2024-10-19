package com.ecom.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Controller;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

   @Column(length = 500)
    private String title;

   @Column(length = 5000)
    private String description;

    private String category;

    private Double price;

    private int stock;

    private String imageName;

    private int discount;

    private Double discountPrice;

    private Boolean isActive;
}
