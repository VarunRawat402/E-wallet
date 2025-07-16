package com.example.wallet.wallet;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TxnController {

    @Autowired
    TxnService txnService;

    /**
     * SenderId
     * ReceiverId
     * Reason
     * @return
     */

    @PostMapping("/txn")
    public ResponseEntity<String> initiateTxn(@RequestParam("receiver") String receiver,
                                      @RequestParam("purpose") String purpose,
                                      @RequestParam("amount") Double amount) throws JsonProcessingException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) authentication.getPrincipal();
        String txnId = txnService.initiateTxn(user.getUsername(), receiver, purpose, amount);
        return new ResponseEntity<>(txnId,HttpStatus.CREATED);
    }
}
