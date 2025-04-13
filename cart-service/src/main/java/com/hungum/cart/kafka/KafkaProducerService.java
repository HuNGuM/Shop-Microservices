package com.hungum.cart.kafka;

import com.hungum.common.dto.ProductDto;
import com.hungum.common.event.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;


    private final String PRODUCT_REQUEST_TOPIC = "product.request";  

    private CompletableFuture<UserResponseEvent> userResponseFuture;
    private CompletableFuture<ProductDto> productResponseFuture;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public UserResponseEvent sendUserRequestEvent(UserRequestEvent event) {
        userResponseFuture = new CompletableFuture<>(); 

        kafkaTemplate.send("user.details.request", event);

        try {
            return userResponseFuture.get();  
        } catch (Exception e) {
             
            e.printStackTrace();
            return null;   
        }
    }

     
    public ProductDto sendProductRequestEvent(ProductRequestEvent event) {
        productResponseFuture = new CompletableFuture<>();   

         
        kafkaTemplate.send(PRODUCT_REQUEST_TOPIC, event);

        try {
             
            return productResponseFuture.get();   
        } catch (InterruptedException | ExecutionException e) {
             
            e.printStackTrace();
            return null;   
        }
    }

     
    @KafkaListener(topics = "product.response", groupId = "cart-service-group")
    public void listenProductResponse(ProductDto response) {
         
        productResponseFuture.complete(response);
    }

}
