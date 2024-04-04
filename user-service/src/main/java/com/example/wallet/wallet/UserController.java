package com.example.wallet.wallet;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    //To create the User
    //Need to pass the UserCreate Request
    @PostMapping("/user")
    public void createUser(@RequestBody UserCreateRequest userCreateRequest) throws JsonProcessingException {
        userService.create(userCreateRequest);
    }

    //To get the User Details
    @GetMapping("/user")
    public User getUserDetails(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return userService.loadUserByUsername(user.getPhoneNumber());
    }

    //To get the All User Details
    @GetMapping("/admin/all/users")
    public List<User> getAllUserDetails(){
        return userService.getAll();
    }

    @GetMapping("/admin/user/{userId}")
    public User getUserDetails(@PathVariable("userId") String userId){
        return userService.loadUserByUsername(userId);
    }
}
