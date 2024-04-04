package com.example.wallet.wallet;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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

    private String country;
    private String dob;

    @NotBlank
    private String identifierValue;
    @NotNull
    private UserIdentifier userIdentifier;

    public User to(){
        return User.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .email(email)
                .password(password)
                .country(country)
                .dob(dob)
                .identifierValue(identifierValue)
                .userIdentifier(userIdentifier)
                .build();
    }


}
