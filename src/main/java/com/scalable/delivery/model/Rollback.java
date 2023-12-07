package com.scalable.delivery.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Rollback {
    String order_id;
    String username;
    String item_name;
    Long amount;
    Double price;
    String message_response;
}
