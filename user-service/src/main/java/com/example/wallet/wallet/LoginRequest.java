package com.example.wallet.wallet;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoginRequest {

    private String phoneNumber;
    private String password;
}
