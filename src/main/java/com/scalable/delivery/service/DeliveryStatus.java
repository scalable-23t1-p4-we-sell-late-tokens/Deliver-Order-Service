package com.scalable.delivery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalable.delivery.model.DeliveryMessage;

import redis.clients.jedis.Jedis;

@Service
public class DeliveryStatus {

    @Autowired
    ObjectMapper objectMapper;
    
    Jedis jedis = new Jedis("redis", 6379);

    // Sends the progress to order service
    public void toOrder(String id, String message_flag) {
        try {
            DeliveryMessage deliveryMessage = new DeliveryMessage(id, message_flag);
            String message = objectMapper.writeValueAsString(deliveryMessage);
            jedis.publish("deliveryToOrder", message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }
}
