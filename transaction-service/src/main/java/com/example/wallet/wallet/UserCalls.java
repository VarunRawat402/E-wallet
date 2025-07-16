package com.example.wallet.wallet;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserCalls {

    @Autowired
    RestTemplate restTemplate;

    public User getUserFromUserService(String phoneNumber, String token) {


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        JSONObject requestedUser= restTemplate.exchange("http://localhost:6001/user/" + phoneNumber,
                HttpMethod.GET, entity, JSONObject.class)
        .getBody();

        List<SimpleGrantedAuthority> authorities = ((List<Map<String, String>>) requestedUser.get("authorities"))
                .stream()
                .map(auth -> new SimpleGrantedAuthority(auth.get("authority")))
                .collect(Collectors.toList());

        return new User(
                (String)requestedUser.get("username"),
                (String)requestedUser.get("password"),
                authorities);
    }

    public JSONObject getUserwithAdmin(String phoneNumber){

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBasicAuth("admin", "admin123");
            HttpEntity request = new HttpEntity(httpHeaders);

            return restTemplate.exchange("http://localhost:6001/user/" + phoneNumber,
                            HttpMethod.GET, request, JSONObject.class)
                    .getBody();
    }

}
        //Creating httpHeaders to give the authentication

