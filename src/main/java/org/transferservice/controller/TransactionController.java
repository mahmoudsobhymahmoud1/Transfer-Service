package org.transferservice.controller;


import jakarta.validation.Valid;
import lombok.Data;
//import org.hibernate.Transaction;
import org.transferservice.dto.TransactionDTO;
import org.transferservice.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.transferservice.repository.TransactionRepository;
import org.transferservice.service.TransactionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Validated
@Data
public class TransactionController {


    private final TransactionService transactionService;


    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {

        Optional<Transaction> transaction = transactionService.getTransactionById(id);
        return transaction.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());


    }

    @GetMapping("/transactionHistory")
    public ResponseEntity<List<Transaction>> getTransactionHistory(
            @RequestParam
            Long accountId,

            @RequestParam(required = false)
            LocalDateTime startDate,

            @RequestParam(required = false)
            LocalDateTime endDate) {

        List<Transaction> transactions = transactionService.getTransactionHistory(accountId, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }


    @PostMapping("/transfer")
    public ResponseEntity<Transaction> createTransaction(@RequestBody @Valid TransactionDTO transactionDTO) {
        Transaction createdTransaction = transactionService.createTransaction(transactionDTO);
        return ResponseEntity.ok(createdTransaction);
    }





}
