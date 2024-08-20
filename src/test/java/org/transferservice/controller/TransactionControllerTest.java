package org.transferservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.transferservice.dto.TransactionDTO;
import org.transferservice.model.Account;
import org.transferservice.model.Transaction;
import org.transferservice.service.TransactionService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


public class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private Transaction transaction;
    private TransactionDTO transactionDTO;

    private Account senderAccount;
    private Account recipientAccount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        transactionDTO = TransactionDTO.builder()
                .senderAccountId(1L)
                .recipientAccountId(2L)
                .amount(100.0)
                .currency("USD")
                .description("Payment")
                .build();

        transaction = Transaction.builder()
                .id(1L)
                .senderAccount(senderAccount)
                .recipientAccount(recipientAccount)
                .amount(100.0)
                .currency("USD")
                .transactionDate(LocalDateTime.now())
                .status("Completed")
                .description("Payment")
                .build();
    }

    @Test
    void getTransactionById_success() {
        when(transactionService.getTransactionById(1L)).thenReturn(Optional.of(transaction));

        ResponseEntity<Transaction> response = transactionController.getTransactionById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void getTransactionById_notFound() {
        when(transactionService.getTransactionById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Transaction> response = transactionController.getTransactionById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    @Test
    void getTransactionHistory_success() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Long accountId = 1L;
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        when(transactionService.getTransactionHistory(accountId, startDate, endDate)).thenReturn(transactions);

        ResponseEntity<List<Transaction>> response = transactionController.getTransactionHistory(accountId, startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void createTransaction_success() {
        when(transactionService.createTransaction(any(TransactionDTO.class))).thenReturn(transaction);

        ResponseEntity<Transaction> response = transactionController.createTransaction(transactionDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }


}
