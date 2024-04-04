package com.example.wallet.wallet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TxnService implements UserDetailsService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    private static Logger logger = LoggerFactory.getLogger(TxnService.class);
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //Getting the User object from the userRepository/userService
        JSONObject requestedUser = getUserFromUserService(username);

        //Listing authorities to new simple authority why we don't do this in userService
        //because we convert these while storing the user object
        List<GrantedAuthority> authorities;
        List<LinkedHashMap<String, String>> requestAuthorities = (List<LinkedHashMap<String, String>>)requestedUser.get("authorities");
        authorities = requestAuthorities
                .stream()
                .map(x -> x.get("authority"))
                .map(x -> new SimpleGrantedAuthority(x))
                .collect(Collectors.toList());

        //returning the user
        return new User(
                (String)requestedUser.get("username"),
                (String)requestedUser.get("password"),
                authorities
        );
    }

    public String initiateTxn(String sender, String receiver, String purpose, Double amount) throws JsonProcessingException {

        //Created a Transaction
        Transaction transaction = Transaction.builder()
                .sender(sender)
                .receiver(receiver)
                .purpose(purpose)
                .transactionId(UUID.randomUUID().toString())
                .transactionStatus(TransactionStatus.PENDING)
                .amount(amount)
                .build();

        transactionRepository.save(transaction);

        //Created the Json Object and sets the details which we will send to the
        //kafka as a msg
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sender", sender);
        jsonObject.put("receiver", receiver);
        jsonObject.put("amount", amount);
        jsonObject.put("txnId", transaction.getTransactionId());

        //Sending the Json object to the Transaction Created topic on kafka
        kafkaTemplate.send(CommonConstants.TRANSACTION_CREATION_TOPIC, objectMapper.writeValueAsString(jsonObject));
        return transaction.getTransactionId();
    }

    //This function will listen the msg produced in the wallet updated topic
    //and will get the msg from the kafka
    @KafkaListener(topics = CommonConstants.WALLET_UPDATED_TOPIC, groupId = "grp123")
    public void updateTxn(String msg) throws ParseException, JsonProcessingException {

        //Parsing the String msg to Json Object
        JSONObject data = (JSONObject) new JSONParser().parse(msg);

        //Fetching the details from the json
        String txnId = (String) data.get("txnId");
        String sender = (String) data.get("sender");
        String receiver = (String) data.get("receiver");
        Double amount = (Double) data.get("amount");

        WalletUpdateStatus walletUpdateStatus = WalletUpdateStatus.valueOf((String)data.get("walletUpdateStatus"));

        JSONObject senderObj = getUserFromUserService(sender);
        String senderEmail = (String)senderObj.get("email");

        String receiverEmail = null;

        if(walletUpdateStatus == WalletUpdateStatus.SUCCESS){
            JSONObject receiverObj = getUserFromUserService(receiver);
            receiverEmail = (String)receiverObj.get("email");
            transactionRepository.updateTxn(txnId, TransactionStatus.SUCCESSFUL);
        }else{
            transactionRepository.updateTxn(txnId, TransactionStatus.FAILED);
        }

        String senderMsg = "Hi, your transaction with id " + txnId + " got " + walletUpdateStatus;

        JSONObject senderEmailObj = new JSONObject();
        senderEmailObj.put("email", senderEmail);
        senderEmailObj.put("msg", senderMsg);

        kafkaTemplate.send(CommonConstants.TRANSACTION_COMPLETED_TOPIC, objectMapper.writeValueAsString(senderEmailObj));

        if(walletUpdateStatus == WalletUpdateStatus.SUCCESS){
            String receiverMsg = "Hi, you have received Rs." + amount + " from "
                    + sender + " in your wallet linked with phone number " + receiver;
            JSONObject receiverEmailObj = new JSONObject();
            receiverEmailObj.put("email", receiverEmail);
            receiverEmailObj.put("msg", receiverMsg);

            kafkaTemplate.send(CommonConstants.TRANSACTION_COMPLETED_TOPIC, objectMapper.writeValueAsString(senderEmailObj));
        }
    }

    /*
    This function is used to get the user from the user Database
    This will run the function on the user Controller which extracts the user from userDB
     */
    private JSONObject getUserFromUserService(String username){
        //Creating httpHeaders to give the authentication
        HttpHeaders httpHeaders = new HttpHeaders();

        //Sets the username and passwords
        httpHeaders.setBasicAuth("admin", "admin123");

        //Created the entity to send
        HttpEntity request = new HttpEntity(httpHeaders);

        //running the api as url and passing the httpEntity request which will give the user object
        //from the database based on the username
        return restTemplate.exchange("http://localhost:6001/admin/user/" + username,
                        HttpMethod.GET, request, JSONObject.class)
                .getBody();
    }
}