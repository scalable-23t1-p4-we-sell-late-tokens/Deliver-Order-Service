package com.scalable.delivery.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalable.delivery.model.Order;
import com.scalable.delivery.model.Rollback;
import com.scalable.delivery.model.RollbackMessage;

import io.micrometer.core.instrument.MeterRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import redis.clients.jedis.Jedis;

@Service
public class MessageSubscriber implements MessageListener {

    @Autowired
    DeliveryStatus deliveryStatus;

    private final MeterRegistry registry;
    private final ObjectMapper objectMapper;

    public MessageSubscriber(MeterRegistry registry, ObjectMapper objectMapper) {
        this.registry = registry;
        this.objectMapper = objectMapper;
    }

    private final Logger LOG = LoggerFactory.getLogger(MessageSubscriber.class);

    // Send progress to Order service on inventory progress
    public void onMessage(Message message, byte[] pattern) {
        try {
            String receivedMessage = new String(message.getBody());
            Order order = objectMapper.readValue(receivedMessage, Order.class);
            if(order.getMessage_flag().equals("delivery")) {
                rollback(receivedMessage);
            } else {
                processMessage(receivedMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processMessage(String message) {
        try {
            Order order = objectMapper.readValue(message, Order.class);
            String id = order.getOrder_id();
            String username = order.getUsername();
            String message_flag = order.getMessage_flag();

            LOG.info("Delivering order " + id + " to " + username);
            registry.counter("delivery.total").increment();

            deliveryStatus.toOrder(id, message_flag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Jedis jedis = new Jedis("redis", 6379);

    public void rollback(String message) {
        try {
            Rollback order = objectMapper.readValue(message, Rollback.class);
            String order_id = order.getOrder_id();
            String username = order.getUsername();
            String item_name = order.getItem_name();
            Long amount = order.getAmount();

            RollbackMessage rollback = new RollbackMessage(order_id, username, item_name, amount, "FAILED");
            String response = objectMapper.writeValueAsString(rollback);
            jedis.publish("deliveryToInventory", response);

            LOG.error("Errored occured, rolling back");
            registry.counter("error.delivery.total").increment();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }
}
