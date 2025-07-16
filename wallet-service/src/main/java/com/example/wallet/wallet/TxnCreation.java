package com.example.wallet.wallet;

import lombok.*;

@Builder
public record TxnCreation(String sender, String receiver, double amount, String txnId) {
}
