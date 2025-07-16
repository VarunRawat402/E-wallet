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

    public void sendUpdatedTxn(TxnUpdate txnUpdate){
        kafkaTemplate.send(CommonConstants.TRANSACTION_COMPLETED_TOPIC, txnUpdate);
    }
}
