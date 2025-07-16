package com.example.wallet.wallet;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j

public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;


    //To create the User
    @PostMapping("/user")
    public ResponseEntity<String> createUser(@RequestBody UserCreateRequest userCreateRequest) throws JsonProcessingException {
        userService.create(userCreateRequest);
        return new ResponseEntity<>("User has been created with username "+ userCreateRequest.getName(),HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest user) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getPhoneNumber(), user.getPassword()));
            UserDetails userDetails = userService.loadUserByUsername(user.getPhoneNumber());
            String token = jwtUtil.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(token, HttpStatus.OK);
        }catch (Exception e){
            log.error("Exception occurred while createAuthenticationToken ", e);
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }
    }

    //To Get Own Details
    @GetMapping("/user")
    public ResponseEntity<User> getUserDetails(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(user);
    }

    //To get the All User Details
    @GetMapping("/admin/all/users")
    public ResponseEntity<List<User>> getAllUserDetails(){
        return ResponseEntity.ok(userService.getAll());
    }

    //Get The User by UserID
    @GetMapping("/user/{phoneNumber}")
    public ResponseEntity<User> getUserDetails(@PathVariable String phoneNumber){
        User user = userService.loadUserByUsername(phoneNumber);
        return ResponseEntity.ok(user);
    }
}
