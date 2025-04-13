package com.hungum.auth.kafka;

import com.hungum.auth.service.AuthService;
import com.hungum.common.dto.UserDto;
import com.hungum.common.event.*;
import com.hungum.common.dto.ProductDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final String SEND_MAIL_TOPIC = "send.mail";  // Тема для отправки писем
    private final String USER_EXISTS_TOPIC = "user.exists"; // Тема для проверки существования пользователя
    private final String USER_EXISTS_RESPONSE_TOPIC = "user.exists.response"; // Тема для ответа по существованию пользователя
    private final String USER_DETAILS_REQUEST_TOPIC = "user.details.request"; // Тема для запроса данных пользователя
    private final String USER_DETAILS_RESPONSE_TOPIC = "user.details.response"; // Тема для ответа с данными пользователя
    private final String PRODUCT_REQUEST_TOPIC = "product.request";  // Тема для запроса данных о товаре
    private final String PRODUCT_RESPONSE_TOPIC = "product.response";  // Тема для ответа с данными о товаре

    private CompletableFuture<Boolean> userExistsResponseFuture;
    private CompletableFuture<UserDetailsResponseEvent> userDetailsResponseFuture;
    private CompletableFuture<ProductDto> productResponseFuture;
    private AuthService authService;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserCreatedEvent(UserCreatedEvent event) {
        kafkaTemplate.send("user.created", event);
    }

    public boolean sendUserExistsEvent(UserExistsEvent event) {
        userExistsResponseFuture = new CompletableFuture<>();

        kafkaTemplate.send(USER_EXISTS_TOPIC, event);

        try {
            return userExistsResponseFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

    public UserDetailsResponseEvent sendUserDetailsRequest(UserDetailsRequestEvent event) {
        userDetailsResponseFuture = new CompletableFuture<>();

        kafkaTemplate.send(USER_DETAILS_REQUEST_TOPIC, event);

        try {
            return userDetailsResponseFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendSendMailEvent(SendMailEvent event) {
        kafkaTemplate.send(SEND_MAIL_TOPIC, event);
    }

    @KafkaListener(topics = USER_EXISTS_RESPONSE_TOPIC, groupId = "auth-service-group")
    public void listenUserExistsResponse(Boolean exists) {
        userExistsResponseFuture.complete(exists);
    }

    @KafkaListener(topics = USER_DETAILS_RESPONSE_TOPIC, groupId = "auth-service-group")
    public void listenUserDetailsResponse(UserDetailsResponseEvent response) {
        userDetailsResponseFuture.complete(response);
    }

    @KafkaListener(topics = PRODUCT_RESPONSE_TOPIC, groupId = "cart-service-group")
    public void listenProductResponse(ProductDto response) {
        productResponseFuture.complete(response);
    }

    @KafkaListener(topics = "user.details.request", groupId = "user-service-group")
    public void listenUserDetailsRequest(UserDetailsRequestEvent request) {
        try {
            Optional<UserDto> userDtoOptional = authService.getCurrentUser();

            if (userDtoOptional.isPresent()) {
                UserDetailsResponseEvent response = new UserDetailsResponseEvent(userDtoOptional.get());

                kafkaTemplate.send("user.details.response", response);
            } else {
                kafkaTemplate.send("user.details.response", new UserDetailsResponseEvent(null));
            }

        } catch (Exception e) {
            e.printStackTrace();
            kafkaTemplate.send("user.details.response", new UserDetailsResponseEvent(null));
        }
    }
}
