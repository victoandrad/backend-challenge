package com.picpay.simplificado.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.picpay.simplificado.domain.transaction.Transaction;
import com.picpay.simplificado.domain.user.User;
import com.picpay.simplificado.domain.user.UserType;
import com.picpay.simplificado.dtos.TransactionDTO;
import com.picpay.simplificado.repositories.TransactionRepository;
import com.picpay.simplificado.services.exceptions.DatabaseException;
import com.picpay.simplificado.services.exceptions.ResourceNotFoundException;
import com.picpay.simplificado.services.exceptions.UnauthorizedException;
import org.hibernate.dialect.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final NotificationService notificationService;

    @Value("${authorization.url}")
    private String authorizationUrl;

    @Autowired
    public TransactionService(UserService userService,TransactionRepository repository, RestTemplate restTemplate, NotificationService notificationService) {
        this.userService = userService;
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.notificationService = notificationService;
    }

    public Transaction createTransactionFromDTO(TransactionDTO dto) throws Exception {
        User sender = this.userService.findById(dto.senderId());
        User receiver = this.userService.findById(dto.receiverId());
        BigDecimal amount = dto.value();

        this.validateTransaction(sender, amount);
        boolean isAuthorized = this.authorizeTransaction();

        if (!isAuthorized) {
            throw new UnauthorizedException("Transaction not authorized");
        }

        Transaction transaction = new Transaction(null, sender, receiver, amount, Instant.now());
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        this.userService.insert(sender);
        this.userService.insert(receiver);
        this.notificationService.sendNotification(sender, "Your transaction has been successfully created");
        this.notificationService.sendNotification(receiver, "You have received your transaction");
        return this.insert(transaction);
    }

    public void validateTransaction(User sender, BigDecimal amount) throws Exception {
        if (sender.getUserType().equals(UserType.MERCHANT)) {
            throw new UnauthorizedException("Sender can not to do transactions");
        }
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new UnauthorizedException("Sender do not have enough money");
        }
    }

    public boolean authorizeTransaction() throws Exception {
        try {
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(authorizationUrl, JsonNode.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode authorizationNode = response.getBody().path("data").path("authorization");
                return authorizationNode.isBoolean() && authorizationNode.asBoolean();
            }
        } catch (Exception e) {
            System.err.println("Error authorizing transaction: " + e.getMessage());
        }
        return false;
    }

    public Transaction findById(Long id) throws Exception {
        Optional<Transaction> transaction = this.repository.findById(id);
        return transaction.orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
    }

    public List<Transaction> findAll() throws Exception {
        return this.repository.findAll();
    }

    public Transaction insert(Transaction transaction) throws Exception {
        try {
            return this.repository.save(transaction);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public void delete(Long id) throws Exception {
        try {
            this.repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public Transaction update(Long id, TransactionDTO dto) throws Exception {
        Transaction transaction = this.findById(id);
        transaction.setSender(userService.findById(dto.senderId()));
        transaction.setReceiver(userService.findById(dto.receiverId()));
        transaction.setAmount(dto.value());
        return this.insert(transaction);
    }
}
