package com.picpay.simplificado.controllers;

import com.picpay.simplificado.domain.transaction.Transaction;
import com.picpay.simplificado.dtos.TransactionDTO;
import com.picpay.simplificado.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/transactions")
public class TransactionController {

    private final TransactionService service;

    @Autowired
    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Transaction> findById(@PathVariable Long id) throws Exception {
        Transaction transaction = this.service.findById(id);
        return ResponseEntity.ok().body(transaction);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> findAll() throws Exception {
        List<Transaction> list = this.service.findAll();
        return ResponseEntity.ok().body(list);
    }

    @PostMapping
    public ResponseEntity<Transaction> insert(@RequestBody TransactionDTO dto) throws Exception {
        Transaction transaction = this.service.createTransactionFromDTO(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{id}")
                .buildAndExpand(transaction.getId())
                .toUri();
        return ResponseEntity.created(location).body(transaction);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws Exception {
        this.service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Transaction> update(@PathVariable Long id, @RequestBody TransactionDTO dto) throws Exception {
        Transaction transaction = this.service.update(id, dto);
        return ResponseEntity.ok().body(transaction);
    }
}
