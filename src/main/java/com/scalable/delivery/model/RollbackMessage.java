package com.scalable.delivery.model;

public record RollbackMessage(String order_id, String username, String item_name, Long amount, String message_response) {
    
}
