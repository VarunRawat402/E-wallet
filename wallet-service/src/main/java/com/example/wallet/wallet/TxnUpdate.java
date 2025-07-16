package com.example.wallet.wallet;

import lombok.*;

@Builder
public record TxnUpdate(String txnId, String sender, String receiver, double amount, WalletUpdateStatus walletUpdateStatus) {

}
