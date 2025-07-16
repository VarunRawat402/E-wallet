package com.example.wallet.wallet;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
@RequiredArgsConstructor
public class KafkaMessaging {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendUserCreation(UserCreation userCreation) {
        kafkaTemplate.send(CommonConstants.USER_CREATION_TOPIC, userCreation);
    }
}

//Message<UserCreation> message = MessageBuilder
//        .withPayload(userCreation)
//        // Add a custom header for the identifier type
//        .setHeader("type", "userCreation")  // This is where you add the identifier
//        .setHeader(TOPIC, CommonConstants.USER_CREATION_TOPIC)
//        .build();
//
//        kafkaTemplate.send(message);