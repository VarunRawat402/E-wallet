package com.example.wallet.wallet;

import jakarta.validation.constraints.*;
import lombok.*;
import org.modelmapper.ModelMapper;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserCreateRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String phoneNumber; //Will use as a username for the user in the application

    @NotBlank
    private String email;
    @NotBlank
    private String password;

    @NotBlank
    private String identifierValue;
    @NotNull
    private UserIdentifier userIdentifier;

    public User toUser(){
        return User.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .email(email)
                .password(password)
                .identifierValue(identifierValue)
                .userIdentifier(userIdentifier)
                .build();
    }

    public UserCreation toUserCreation(){
        return UserCreation.builder()
                .phoneNumber(phoneNumber)
                .identifierValue(identifierValue)
                .userIdentifier(userIdentifier)
                .build();
    }
}