package com.picpay.simplificado.services;

import com.picpay.simplificado.domain.transaction.Transaction;
import com.picpay.simplificado.domain.user.User;
import com.picpay.simplificado.dtos.TransactionDTO;
import com.picpay.simplificado.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Service
public class TransactionService {

    private final UserService userService;
    private final TransactionRepository repository;
    private final RestTemplate restTemplate;

    @Autowired
    public TransactionService(UserService userService, TransactionRepository repository, RestTemplate restTemplate) {
        this.userService = userService;
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    public Transaction createTransaction(TransactionDTO transactionDTO) throws Exception {
        User sender = this.userService.findById(transactionDTO.senderId());
        User receiver = this.userService.findById(transactionDTO.receiverId());
        BigDecimal amount = transactionDTO.value();

        userService.validateTransaction(sender, amount);

        if (!this.authorizeTransaction()) {
            throw new Exception("Transaction not authorized");
        }

        Transaction transaction = new Transaction(null, sender, receiver, amount, Instant.now());
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        this.userService.insert(sender);
        this.userService.insert(receiver);
        return this.repository.save(transaction);
    }

    public boolean authorizeTransaction() throws Exception {
        ResponseEntity<Map> response = restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", Map.class);

        if (response.getStatusCode().equals(HttpStatus.OK)) {
            return (Boolean) response.getBody().get("authorization");
        }
        throw new Exception("Internal Server Error");
    }
}
