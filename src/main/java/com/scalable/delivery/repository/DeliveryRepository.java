package com.scalable.delivery.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.scalable.delivery.model.Delivery;

import java.util.Optional;

@Repository
public interface DeliveryRepository extends CrudRepository<Delivery, String> {
    Optional<Delivery> findByUsername(String itemName);
}
