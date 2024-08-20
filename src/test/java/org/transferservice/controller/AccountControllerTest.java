package org.transferservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.transferservice.dto.enums.AccountCurrency;
import org.transferservice.dto.enums.AccountType;
import org.transferservice.model.Account;
import org.transferservice.service.AccountService;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.transferservice.dto.enums.AccountCurrency.USD;
import static org.transferservice.dto.enums.AccountType.CHECKING;
import static org.transferservice.dto.enums.AccountType.SAVINGS;

public class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        account = new Account();
        account.setId(1L);
        account.setAccountName("Test Account");
        account.setAccountDescription("Test Description");
        account.setBalance(1000.0);
        account.setAccountType(SAVINGS);
        account.setCurrency(USD);
        account.setActive(true);
    }

    @Test
    void createAccount_success() {
        when(accountService.createAccount(any(Account.class))).thenReturn(account);

        ResponseEntity<Account> response = accountController.createAccount(account);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Account", response.getBody().getAccountName());
    }

    @Test
    void getAccountBalance_success() {
        when(accountService.getAccountBalance(1L)).thenReturn(1000.0);

        ResponseEntity<Double> response = accountController.getAccountBalance(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1000.0, response.getBody());
    }

    @Test
    void getAccountById_success() {
        when(accountService.getAccountById(1L)).thenReturn(Optional.of(account));

        ResponseEntity<Account> response = accountController.getAccountById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Account", response.getBody().getAccountName());
    }

    @Test
    void getAccountById_notFound() {
        when(accountService.getAccountById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Account> response = accountController.getAccountById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateAccount_success() {
        Account updatedAccountDetails = new Account();
        updatedAccountDetails.setAccountName("Updated Account");
        updatedAccountDetails.setAccountDescription("Updated Description");
        updatedAccountDetails.setBalance(2000.0);
        updatedAccountDetails.setAccountType(CHECKING);
        updatedAccountDetails.setCurrency(AccountCurrency.valueOf("EUR"));
        updatedAccountDetails.setActive(false);

        when(accountService.getAccountById(1L)).thenReturn(Optional.of(account));
        when(accountService.updateAccount(any(Account.class))).thenReturn(updatedAccountDetails);

        ResponseEntity<Account> response = accountController.updateAccount(1L, updatedAccountDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Account", response.getBody().getAccountName());
    }

    @Test
    void updateAccount_notFound() {
        Account updatedAccountDetails = new Account();
        updatedAccountDetails.setAccountName("Updated Account");

        when(accountService.getAccountById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Account> response = accountController.updateAccount(1L, updatedAccountDetails);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteAccount_success() {
        when(accountService.getAccountById(1L)).thenReturn(Optional.of(account));
        doNothing().when(accountService).deleteAccount(1L);

        ResponseEntity<Void> response = accountController.deleteAccount(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(accountService, times(1)).deleteAccount(1L);
    }

    @Test
    void deleteAccount_notFound() {
        when(accountService.getAccountById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = accountController.deleteAccount(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(accountService, never()).deleteAccount(1L);
    }


}
