package com.hungum.user.kafka;

import com.hungum.common.dto.UserDto;
import com.hungum.common.event.*;
import com.hungum.user.model.User;
import com.hungum.user.repository.UserRepository;
import com.hungum.user.service.UserDetailServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserDetailServiceImpl userDetailService;



    @KafkaListener(topics = "user.created", groupId = "user-group")
    public void consume(UserCreatedEvent event) {
        log.info("Received user created event: {}", event);

        User user = new User(event.getEmail(), event.getUsername(), event.getEncodedPassword());
        user.setEnabled(false);

        userRepository.save(user);
    }
         @KafkaListener(topics = "user.details.request", groupId = "user-service-group")
    public void listenUserDetailsRequest(UserDetailsRequestEvent request) {
        try {
                         UserDto userDto = userDetailService.getUserDetailsByUsername(request.getUsername());

                         UserDetailsResponseEvent response = new UserDetailsResponseEvent(userDto);

                         kafkaTemplate.send("user.details.response", response);

        } catch (Exception e) {
            e.printStackTrace();
                     }
    }
    @KafkaListener(topics = "user.request", groupId = "user-service-group")
    public void listenUserRequest(UserRequestEvent event) {
        User user = userRepository.findByUsername(event.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponseEvent responseEvent = new UserResponseEvent(user.getUsername(), user.getEmail(), user.isEnabled());

        kafkaTemplate.send("user.response", responseEvent);       }

}
