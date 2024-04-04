package com.example.wallet.wallet;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    /*
    We can return User here instead of UserDetails because user is a child class and UserDetails is
    a parent class and user will inherit all the properties of parent class
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }*/
    @Override
    public User loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    /* To create the User and save in the DB
       Convert the UserCreateRequest to User
       Get the User password and encrypt the password using encryptPwd()
       Set the password and save in the DB
    */
    public void create(UserCreateRequest userCreateRequest) throws JsonProcessingException {
        User user = userCreateRequest.to();
        user.setPassword(encryptPwd(user.getPassword()));
        user.setAuthorities(UserConstants.USER_AUTHORITY);
        user = userRepository.save(user);

        /*
        TODO : We need to publish the event after creating the user which can be listened by
           consumers to perform functions based after creating the user
        */

        //Created a json Object with and put details we want to send to the listener
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", user.getId());
        jsonObject.put("phoneNumber", user.getPhoneNumber());
        jsonObject.put("identifierValue", user.getIdentifierValue());
        jsonObject.put("userIdentifier", user.getUserIdentifier());

        //Sent the message( Json Object ) to the topic USER_CREATION_TOPIC
        //Anyone who wants to listen to that msg and take the values can take it
        kafkaTemplate.send(CommonConstants.USER_CREATION_TOPIC,
                objectMapper.writeValueAsString(jsonObject));
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
