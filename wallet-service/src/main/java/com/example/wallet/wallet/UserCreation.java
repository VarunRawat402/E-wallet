package com.example.wallet.wallet;

import lombok.*;

@Builder
public record UserCreation (String phoneNumber, UserIdentifier userIdentifier, String identifierValue) {
}
