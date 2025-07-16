package com.example.wallet.wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class UserConfig {

    @Bean
    PasswordEncoder getPE(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    ObjectMapper getMapper(){
        return new ObjectMapper();
    }

    @Bean
    ModelMapper getMM(){
        return new ModelMapper();
    }

    @Bean
    public NewTopic userCreationTopic(){
        return TopicBuilder.name(CommonConstants.USER_CREATION_TOPIC)
                .build();
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

}

//    @Bean
//    public Map<String, Object> getPProperties() {
//        Map<String, Object> properties = new HashMap<>();
//        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        // Configure the JsonSerializer for the value
//        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//
//        // Add the type mapping programmatically here for the producer
//        properties.put("spring.json.type.mapping", "userCreation:com.example.wallet.wallet.UserCreation");
//        return properties;
//    }
//
//    // ProducerFactory
//    public ProducerFactory<String, Object> getProducerFactory() {
//        return new DefaultKafkaProducerFactory<>(getPProperties());
//    }

