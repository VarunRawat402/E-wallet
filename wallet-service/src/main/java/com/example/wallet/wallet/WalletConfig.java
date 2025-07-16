package com.example.wallet.wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class WalletConfig {

    @Bean
    ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public NewTopic walletUpdate() {
        return TopicBuilder.name(CommonConstants.WALLET_UPDATED_TOPIC).build();
    }

    @Bean
    RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

}

//    public Map<String, Object> getCProperties() {
//
//        Map<String, Object> properties = new HashMap<>();
//        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.wallet.wallet");
//        properties.put(JsonDeserializer.TYPE_MAPPINGS,
//                "userCreation:com.example.wallet.wallet.UserCreation,"
//                        + "txnCreation:com.example.wallet.wallet.TxnCreation,"
//                        + "txnUpdate:com.example.wallet.wallet.TxnUpdate"
//        );
//        properties.put(JsonDeserializer.VALUE_DEFAULT_TYPE, UserCreation.class);
//        return properties;
//    }


//    // ConsumerFactory
//    public ConsumerFactory<String, Object> getConsumerFactory() {
//        return new DefaultKafkaConsumerFactory<>(getCProperties());
//    }

//    // Producer Config
//    public Map<String, Object> getPProperties() {
//        Map<String, Object> properties = new HashMap<>();
//        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        return properties;
//    }
//
//    // ProducerFactory
//    public ProducerFactory<String, Object> getProducerFactory() {
//        return new DefaultKafkaProducerFactory<>(getPProperties());
//    }
