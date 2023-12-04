package com.scalable.delivery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scalable.delivery.exception.UserNotFoundException;
import com.scalable.delivery.model.Delivery;
import com.scalable.delivery.repository.DeliveryRepository;

import java.util.Optional;


@Service
public class DeliveryService {
    @Autowired
    private DeliveryRepository deliveryRepository;

    public void createDefaultUser(String username) {
        Optional<Delivery> entity = deliveryRepository.findByUsername(username);
        if(entity.isEmpty()) {
            Delivery newPayment = new Delivery(username);
            deliveryRepository.save(newPayment);
        }
    }

    public void createNewUser(String itemName, long stock) {
        Optional<Delivery> entity = deliveryRepository.findByUsername(itemName);
        if(entity.isEmpty()) {
            Delivery newPayment = new Delivery(itemName, stock);
            deliveryRepository.save(newPayment);
        }
    }

    public Optional<Delivery> getUser(String username) {
        return deliveryRepository.findByUsername(username);
    }


    public void deliverTo(String username, long amount) throws Exception{
        Delivery user = deliveryRepository.findByUsername(username).orElse(null);

        if (user != null) {
            user.setStock(user.getStock() + amount);
            deliveryRepository.save(user);
        } else {
            throw new UserNotFoundException("Item: " + username + " not found");
        }
    }

}
