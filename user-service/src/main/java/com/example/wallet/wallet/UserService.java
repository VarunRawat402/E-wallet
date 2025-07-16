package com.example.wallet.wallet;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaMessaging kafkaMessaging;
    private final ObjectMapper objectMapper;


    @Override
    public User loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public void create(UserCreateRequest userCreateRequest) throws JsonProcessingException {

        //Created and saved the User in Database
        User user = userCreateRequest.toUser();
        user.setPassword(encryptPwd(user.getPassword()));
        user.setAuthorities(UserConstants.USER_AUTHORITY);
        user = userRepository.save(user);

        //Send the UserCreation data to kafka
        UserCreation userCreation = userCreateRequest.toUserCreation();
        kafkaMessaging.sendUserCreation(userCreation);
    }

    //To get All the Users
    public List<User> getAll(){
        return userRepository.findAll();
    }

    //To encrypt the raw password using password Encoder
    private String encryptPwd(String rawPwd){
        return passwordEncoder.encode(rawPwd);
    }
}
