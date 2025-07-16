package com.example.wallet.wallet;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class WalletService {


    private final WalletRepository walletRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RestTemplate restTemplate;
    private final KafkaMessaging kafkaMessaging;

    //This function will listen to the topic User_Creation and when ever a message is produced in that topic
    //This will get run and that message is passed in this as a parameter
    @KafkaListener(topics = CommonConstants.USER_CREATION_TOPIC, groupId = "grp123")
    public void createWallet(UserCreation userCreation){

        String phoneNumber = userCreation.phoneNumber();
        String identifierKey = String.valueOf(userCreation.userIdentifier());
        String identifierValue = userCreation.identifierValue();

        //Creating a wallet for that user with extracted user details
        Wallet wallet = Wallet.builder()
                .phoneNumber(phoneNumber)
                .userIdentifier(UserIdentifier.valueOf(identifierKey))
                .identifierValue(identifierValue)
                .balance(1000.0)
                .build();
        walletRepository.save(wallet);
    }


    @KafkaListener(topics = CommonConstants.TRANSACTION_CREATION_TOPIC, groupId = "grp123")
    public void updateWalletsForTxn(TxnCreation txnCreation){

        String sender = txnCreation.sender();
        String receiver = txnCreation.receiver();
        Double amount = txnCreation.amount();
        String txnId = txnCreation.txnId();

        //Extracting sender and receiver wallet object to check the amounts in their bank
        Wallet senderWallet = walletRepository.findByPhoneNumber(sender);
        Wallet receiverWallet = walletRepository.findByPhoneNumber(receiver);

        //If no sender or receiver wallet then txn will fail
        //If balance is less than amount then txn will fail
        //Send the status as FAILED to the wallet_updated topic
        if(senderWallet == null || receiverWallet == null
                || senderWallet.getBalance() < amount){

            TxnUpdate txnUpdate = new TxnUpdate(txnId,sender,receiver,amount,WalletUpdateStatus.FAILED);
            kafkaMessaging.sendUpdatedTxn(txnUpdate);
            return;
        }

        //Update the amount in wallet
        walletRepository.updateWallet(receiver, amount); // +10
        walletRepository.updateWallet(sender, 0 - amount);  // -10

        //Send the status as SUCCESS to the wallet_updated topic
        TxnUpdate txnUpdate = new TxnUpdate(txnId,sender,receiver,amount,WalletUpdateStatus.SUCCESS);
        kafkaMessaging.sendUpdatedTxn(txnUpdate);
    }
}