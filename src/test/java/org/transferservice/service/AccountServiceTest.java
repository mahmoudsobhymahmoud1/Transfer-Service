package org.transferservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.transferservice.model.Account;
import org.transferservice.repository.AccountRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account account;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .id(1L)
                .accountNumber("123456789")
                .balance(1000.0)
                .accountName("Alex's Account")
                .accountDescription("Personal Savings Account")
                .build();
    }

    @Test
    void createAccount_success() {
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account createdAccount = accountService.createAccount(account);

        assertNotNull(createdAccount);
        assertEquals(1L, createdAccount.getId());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void getAccountById_success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        Optional<Account> foundAccount = accountService.getAccountById(1L);

        assertTrue(foundAccount.isPresent());
        assertEquals("123456789", foundAccount.get().getAccountNumber());
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void getAccountById_notFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Account> foundAccount = accountService.getAccountById(1L);

        assertFalse(foundAccount.isPresent());
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void getAccountBalance_success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        Double balance = accountService.getAccountBalance(1L);

        assertNotNull(balance);
        assertEquals(1000.0, balance);
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void getAccountBalance_accountNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.getAccountBalance(1L);
        });

        assertEquals("Account not found", exception.getMessage());
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void updateAccount_success() {
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account updatedAccount = accountService.updateAccount(account);

        assertNotNull(updatedAccount);
        assertEquals(1L, updatedAccount.getId());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void deleteAccount_success() {
        doNothing().when(accountRepository).deleteById(1L);

        accountService.deleteAccount(1L);

        verify(accountRepository, times(1)).deleteById(1L);
    }
}
