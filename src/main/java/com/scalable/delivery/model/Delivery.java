package com.scalable.delivery.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "delivery")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String username;
    private long stock;

    public Delivery() { }

    public Delivery(String username, long stock) {
        this.username = username;
        this.stock = stock;
    }

    // At creation, user holds no tokens
    public Delivery(String username) {
        this.username = username;
        this.stock = 0;
    }
}
