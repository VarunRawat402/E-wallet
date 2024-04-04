package com.example.wallet.wallet;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class WalletService {


    @Autowired
    WalletRepository walletRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    RestTemplate restTemplate;

    //This function will listen to the topic User_Creation and when ever a message is produced in that topic
    //This will get run and that message is passed in this as a parameter
    @KafkaListener(topics = CommonConstants.USER_CREATION_TOPIC, groupId = "grp123")
    public void createWallet(String msg) throws ParseException {

        //Converting the String msg to Json Object and extracting the values
        JSONObject data = (JSONObject) new JSONParser().parse(msg);

        String phoneNumber = (String)data.get(CommonConstants.USER_CREATION_TOPIC_PHONE_NUMBER);
        Long userId = (Long) data.get(CommonConstants.USER_CREATION_TOPIC_USERID);
        String identifierKey = (String)data.get(CommonConstants.USER_CREATION_TOPIC_IDENTIFIER_KEY);
        String identifierValue = (String)data.get(CommonConstants.USER_CREATION_TOPIC_IDENTIFIER_VALUE);

        //Creating a wallet for that user with extracted user details
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .phoneNumber(phoneNumber)
                .userIdentifier(UserIdentifier.valueOf(identifierKey))
                .identifierValue(identifierValue)
                .balance(100.0)
                .build();

        walletRepository.save(wallet);

    }

    //This function will listen to the topic Tranx_created
    @KafkaListener(topics = CommonConstants.TRANSACTION_CREATION_TOPIC, groupId = "grp123")
    public void updateWalletsForTxn(String msg) throws ParseException, JsonProcessingException {

        JSONObject data = (JSONObject) new JSONParser().parse(msg);

        String sender = (String)data.get("sender");
        String receiver = (String)data.get("receiver");
        Double amount = (Double) data.get("amount");
        String txnId = (String) data.get("txnId");

        //Extracting sender and receiver wallet object to check the amounts in their bank
        Wallet senderWallet = walletRepository.findByPhoneNumber(sender);
        Wallet receiverWallet = walletRepository.findByPhoneNumber(receiver);

        //Created a jsonObject and put the values which is extracted to send back to the trans
        //To give and tell them the updating or deleting is happened for this trans
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("txnId", txnId);
        jsonObject.put("sender", sender);
        jsonObject.put("receiver", receiver);
        jsonObject.put("amount", amount);

        //If sender wallet is 0 or less than the amount he is trying to send
        //put the status as FAILED which will get assigned to the transaction status and send it
        //to the wallet_updated topic
        if(senderWallet == null || receiverWallet == null
                || senderWallet.getBalance() < amount){
            jsonObject.put("walletUpdateStatus", WalletUpdateStatus.FAILED);
            kafkaTemplate.send(CommonConstants.WALLET_UPDATED_TOPIC, objectMapper.writeValueAsString(jsonObject));
            return;
        }

        //Update and delete the wallet
        walletRepository.updateWallet(receiver, amount); // +10
        walletRepository.updateWallet(sender, 0 - amount);  // -10

        //Send the status as SUCCESS to the wallet_updated topic
        // TODO: Kafka event for wallet update
        jsonObject.put("walletUpdateStatus", WalletUpdateStatus.SUCCESS);
        kafkaTemplate.send(CommonConstants.WALLET_UPDATED_TOPIC, objectMapper.writeValueAsString(jsonObject));
    }
}