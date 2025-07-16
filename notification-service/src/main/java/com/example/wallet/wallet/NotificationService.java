package com.example.wallet.wallet;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    SimpleMailMessage simpleMailMessage;
    JavaMailSender javaMailSender;

    @KafkaListener(topics = {CommonConstants.TRANSACTION_COMPLETED_TOPIC}, groupId = "grp123")
    public void sendNotification(SendMail sendMail) throws ParseException {

        String email = sendMail.customerEmail();
        String emailMsg = sendMail.message();

        simpleMailMessage.setFrom("varunrawatstreetboys@gmail.com");
        simpleMailMessage.setTo(email);
        simpleMailMessage.setText(emailMsg);
        simpleMailMessage.setSubject("E wallet Payment Updates");
        javaMailSender.send(simpleMailMessage);
    }
}
