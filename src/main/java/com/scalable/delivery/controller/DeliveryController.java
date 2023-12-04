package com.scalable.delivery.controller;
import com.scalable.delivery.exception.UserNotFoundException;
import com.scalable.delivery.model.Delivery;
import com.scalable.delivery.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("delivery")
public class DeliveryController {
    @Autowired
    DeliveryService deliveryService;

    @PostMapping("/create-default/{username}")
    public ResponseEntity<String> createNewDefaultPayment(@PathVariable String username)
    {
        deliveryService.createDefaultUser(username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create/{username}/{amount}")
    public ResponseEntity<String> createNewPayment(@PathVariable String username, @PathVariable long amount)
    {
        deliveryService.createNewUser(username, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/order/{username}/{amount}")
    public ResponseEntity<String> deliverItem(@PathVariable String username, @PathVariable long amount)
    {
        try {
            deliveryService.deliverTo(username, amount);
        } catch (UserNotFoundException itemNotFoundException) {
            return ResponseEntity.internalServerError().body("User not found");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
        deliveryService.createNewUser(username, amount);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<Long> getUser(@PathVariable String username) {
        Delivery retrievedUsername = deliveryService.getUser(username).orElse(null);
        if (retrievedUsername != null) {
            return ResponseEntity.ok(retrievedUsername.getStock());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
