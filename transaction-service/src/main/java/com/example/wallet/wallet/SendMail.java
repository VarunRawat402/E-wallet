package com.example.wallet.wallet;

import lombok.*;


@Builder
public record SendMail(String customerEmail, String message) {
}

