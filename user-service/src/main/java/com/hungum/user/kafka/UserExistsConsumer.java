package com.hungum.user.kafka;

import com.hungum.common.event.UserExistsEvent;
import com.hungum.user.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserExistsConsumer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    UserRepository userRepository;

    private final String USER_EXISTS_RESPONSE_TOPIC = "user.exists.response";  
    @KafkaListener(topics = "user.exists", groupId = "user-service-group")
    public void checkIfUserExists(UserExistsEvent event) {
                 boolean userExists = userRepository.existsByUsername(event.getRegisterRequestDto().getUsername());

                 kafkaTemplate.send(USER_EXISTS_RESPONSE_TOPIC, userExists);
    }
}
