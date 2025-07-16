package com.example.wallet.wallet;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class TxnConfig {

    @Bean
    RestTemplate getTemplate(){
        return new RestTemplate();
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String,Object> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic txnCreate(){
        return TopicBuilder.name(CommonConstants.TRANSACTION_CREATION_TOPIC).build();
    }

    @Bean
    public NewTopic txnUpdate(){
        return TopicBuilder.name(CommonConstants.TRANSACTION_COMPLETED_TOPIC).build();
    }

}