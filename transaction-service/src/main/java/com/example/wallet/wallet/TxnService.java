package com.example.wallet.wallet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class TxnService implements UserDetailsService {

    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaMessaging kafkaMessaging;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final UserCalls userCalls;

    private static Logger logger = LoggerFactory.getLogger(TxnService.class);
    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        throw new UnsupportedOperationException("loadUserByUsername is not used in JWT flow");
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
        TxnCreation txnCreation = new TxnCreation(sender,receiver,amount,transaction.getTransactionId());
        kafkaMessaging.txnCreationSend(txnCreation);
        return transaction.getTransactionId();
    }

    @KafkaListener(topics = CommonConstants.WALLET_UPDATED_TOPIC, groupId = "grp123")
    public void updateTxn(TxnUpdate txnUpdate) throws ParseException, JsonProcessingException {

        //Fetched the Data
        String txnId = txnUpdate.txnId();
        String sender = txnUpdate.sender();
        String receiver = txnUpdate.receiver();
        Double amount = txnUpdate.amount();
        WalletUpdateStatus walletUpdateStatus = txnUpdate.walletUpdateStatus();

        //Fetched Sender Email
        JSONObject senderObj = userCalls.getUserwithAdmin(sender);
        String senderEmail = (String)senderObj.get("email");

        //Sending Mail to Sender
        if(walletUpdateStatus == WalletUpdateStatus.SUCCESS){
            transactionRepository.updateTxn(txnId, TransactionStatus.SUCCESSFUL);
            System.out.println("Updated success");
        }else{
            transactionRepository.updateTxn(txnId, TransactionStatus.FAILED);
            System.out.println("updated failed");
        }

        String senderMsg = "Hi, your transaction with id " + txnId + " got " + walletUpdateStatus;
        SendMail senderMail = new SendMail(senderEmail,senderMsg);
        kafkaMessaging.txnCompletedSend(senderMail);

        //Sending Mail to receiver
        if(walletUpdateStatus == WalletUpdateStatus.SUCCESS){

            JSONObject receiverObj = userCalls.getUserwithAdmin(receiver);
            String receiverEmail = (String)receiverObj.get("email");

            String receiverMsg = "Congratulations! ₹" + amount + " has been credited to your wallet from "
                    + sender + ".";

            SendMail receiverMail = new SendMail(receiverEmail,receiverMsg);
            kafkaMessaging.txnCompletedSend(receiverMail);
        }
    }
}