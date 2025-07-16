package com.example.wallet.wallet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class UserApplication implements CommandLineRunner {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
        System.out.println("User Service is Started!!!!!!!!!!!!!!");
    }

    @Override
    public void run(String... args) throws Exception {
        User adminUser = User.builder()
                .phoneNumber("admin")
                .password(passwordEncoder.encode("admin123"))
                .authorities(UserConstants.ADMIN_AUTHORITY)
                .email("admin@gmail.com")
                .userIdentifier(UserIdentifier.SERVICE_ID)
                .identifierValue("admin123")
                .build();

        userRepository.save(adminUser);
    }
}
//        User txnServiceUser = User.builder()
//                .phoneNumber("txn_service")
//                .password(passwordEncoder.encode("txn123"))
//                .authorities(UserConstants.SERVICE_AUTHORITY)
//                .email("txn@gmail.com")
//                .userIdentifier(UserIdentifier.SERVICE_ID)
//                .identifierValue("txn123")
//                .build();
//
//        userRepository.save(txnServiceUser);