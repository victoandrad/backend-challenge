package com.picpay.simplificado.services;

import com.picpay.simplificado.domain.transaction.Transaction;
import com.picpay.simplificado.domain.user.User;
import com.picpay.simplificado.domain.user.UserType;
import com.picpay.simplificado.dtos.TransactionDTO;
import com.picpay.simplificado.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TransactionService {

    private final UserService userService;
    private final TransactionRepository repository;
    private final RestTemplate restTemplate;

    @Value("${authorization.url}")
    private String authorizationUrl;

    @Autowired
    public TransactionService(UserService userService, TransactionRepository repository, RestTemplate restTemplate) {
        this.userService = userService;
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    public Transaction createTransactionFromDTO(TransactionDTO dto) throws Exception {
        User sender = this.userService.findById(dto.senderId());
        User receiver = this.userService.findById(dto.receiverId());
        BigDecimal amount = dto.value();

        this.validateTransaction(sender, amount);
        boolean isAuthorized = this.authorizeTransaction();

        if (!isAuthorized) {
            throw new Exception("Transaction not authorized");
        }

        Transaction transaction = new Transaction(null, sender, receiver, amount, Instant.now());
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        this.userService.insert(sender);
        this.userService.insert(receiver);
        return this.insert(transaction);
    }

    public void validateTransaction(User sender, BigDecimal amount) throws Exception {
        if (sender.getUserType().equals(UserType.MERCHANT)) {
            throw new Exception("Sender can not to do transactions");
        }
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new Exception("Sender do not have enough money");
        }
    }

    public boolean authorizeTransaction() throws Exception {
        ResponseEntity<Map> response = restTemplate.getForEntity(authorizationUrl, Map.class);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            Object authorization = response.getBody().get("authorization");
            if (authorization instanceof Boolean) {
                return (Boolean) authorization;
            }
        }
        throw new Exception("Internal Server Error");
    }

    public Transaction findById(Long id) throws Exception {
        Optional<Transaction> transaction = this.repository.findById(id);
        return transaction.orElseThrow(() -> new Exception("Transaction not found"));
    }

    public List<Transaction> findAll() throws Exception {
        return this.repository.findAll();
    }

    public Transaction insert(Transaction transaction) throws Exception {
        return this.repository.save(transaction);
    }

    public void delete(Long id) throws Exception {
        this.repository.deleteById(id);
    }

    public Transaction update(Long id, TransactionDTO dto) throws Exception {
        Transaction transaction = this.findById(id);
        transaction.setSender(userService.findById(dto.senderId()));
        transaction.setReceiver(userService.findById(dto.receiverId()));
        transaction.setAmount(dto.value());
        return this.insert(transaction);
    }
}
