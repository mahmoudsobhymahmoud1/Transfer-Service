package org.transferservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.transferservice.dto.TransactionDTO;
import org.transferservice.model.Account;
import org.transferservice.model.Transaction;
import org.transferservice.repository.AccountRepository;
import org.transferservice.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    private TransactionDTO transactionDTO;
    private Account senderAccount;
    private Account recipientAccount;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transactionDTO = new TransactionDTO();
        transactionDTO.setSenderAccountId(1L);
        transactionDTO.setRecipientAccountId(2L);
        transactionDTO.setAmount(100.0);
        transactionDTO.setCurrency("USD");
        transactionDTO.setStatus("COMPLETED");
        transactionDTO.setDescription("Test transaction");

        senderAccount = new Account();
        senderAccount.setId(1L);
        senderAccount.setBalance(200.0);

        recipientAccount = new Account();
        recipientAccount.setId(2L);
        recipientAccount.setBalance(100.0);

        transaction = new Transaction();
        transaction.setSenderAccount(senderAccount);
        transaction.setRecipientAccount(recipientAccount);
        transaction.setAmount(100.0);
        transaction.setCurrency("USD");
        transaction.setStatus("COMPLETED");
        transaction.setDescription("Test transaction");
    }

    @Test
    void createTransaction_success() {
        when(accountService.getAccountById(1L)).thenReturn(Optional.of(senderAccount));
        when(accountService.getAccountById(2L)).thenReturn(Optional.of(recipientAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction createdTransaction = transactionService.createTransaction(transactionDTO);

        assertNotNull(createdTransaction);
        assertEquals(100.0, createdTransaction.getAmount());
        verify(accountRepository, times(1)).save(senderAccount);
        verify(accountRepository, times(1)).save(recipientAccount);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void createTransaction_insufficientBalance() {
        senderAccount.setBalance(50.0);
        when(accountService.getAccountById(1L)).thenReturn(Optional.of(senderAccount));
        when(accountService.getAccountById(2L)).thenReturn(Optional.of(recipientAccount));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(transactionDTO);
        });

        assertEquals("Insufficient balance in sender's account", exception.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void createTransaction_invalidAccountIds() {
        when(accountService.getAccountById(1L)).thenReturn(Optional.empty());
        when(accountService.getAccountById(2L)).thenReturn(Optional.of(recipientAccount));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(transactionDTO);
        });

        assertEquals("Invalid account IDs", exception.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void getTransactionHistory_withDateRange() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(10);
        LocalDateTime endDate = LocalDateTime.now();
        when(transactionRepository.findBySenderAccountIdOrRecipientAccountIdAndTransactionDateBetween(
                1L, 1L, startDate, endDate)).thenReturn(List.of(transaction));

        List<Transaction> transactions = transactionService.getTransactionHistory(1L, startDate, endDate);

        assertEquals(1, transactions.size());
        verify(transactionRepository, times(1))
                .findBySenderAccountIdOrRecipientAccountIdAndTransactionDateBetween(1L, 1L, startDate, endDate);
    }

    @Test
    void getTransactionHistory_withoutDateRange() {
        when(transactionRepository.findBySenderAccountIdOrRecipientAccountId(1L, 1L))
                .thenReturn(List.of(transaction));

        List<Transaction> transactions = transactionService.getTransactionHistory(1L, null, null);

        assertEquals(1, transactions.size());
        verify(transactionRepository, times(1))
                .findBySenderAccountIdOrRecipientAccountId(1L, 1L);
    }



}
