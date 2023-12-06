package com.scalable.delivery.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalable.delivery.model.Order;

import io.micrometer.core.instrument.MeterRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

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

    // Send progress to Order service on payment progress
    public void onMessage(Message message, byte[] pattern) {
        String receivedMessage = new String(message.getBody());

        processMessage(receivedMessage);
    }

    public void processMessage(String message) {
        try {
            Order order = objectMapper.readValue(message, Order.class);
            String id = order.getId();
            String username = order.getUsername();

            LOG.info("Delivering order " + id + " to " + username);
            registry.counter("delivery.total").increment();

            deliveryStatus.toOrder(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
