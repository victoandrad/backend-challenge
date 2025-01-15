package com.picpay.simplificado.services;

import com.picpay.simplificado.domain.transaction.Transaction;
import com.picpay.simplificado.domain.user.User;
import com.picpay.simplificado.domain.user.UserType;
import com.picpay.simplificado.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public void validateTransaction(Transaction transaction) throws Exception {
        User sender = transaction.getSender();
        BigDecimal amount = transaction.getAmount();

        if (sender.getUserType().equals(UserType.MERCHANT)) {
            throw new Exception("Sender can not to do transactions");
        }

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new Exception("Sender do not have enough money");
        }
    }

    public User findById(Long id) throws Exception {
        Optional<User> user = this.repository.findById(id);
        return user.orElseThrow(() -> new Exception("Resource Not Found"));
    }

    public User insert(User user) {
        return this.repository.save(user);
    }
}
