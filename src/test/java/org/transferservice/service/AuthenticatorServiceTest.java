package org.transferservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.transferservice.dto.CreateCustomerDTO;
import org.transferservice.dto.CustomerDTO;
import org.transferservice.dto.enums.AccountCurrency;
import org.transferservice.dto.enums.AccountType;
import org.transferservice.exception.custom.CustomerAlreadyExistException;
import org.transferservice.model.Account;
import org.transferservice.model.Customer;
import org.transferservice.repository.AccountRepository;
import org.transferservice.repository.CustomerRepository;
import org.transferservice.service.security.AuthenticatorService;
import org.transferservice.service.security.JwtUtils;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.transferservice.dto.enums.Gender.MALE;


public class AuthenticatorServiceTest {
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthenticatorService authenticatorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_success() throws CustomerAlreadyExistException {
        CreateCustomerDTO createCustomerDTO = CreateCustomerDTO.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("1234567890")
                .address("123 Main St")
                .gender(MALE)
                .nationalIdNumber("12345678901234")
                .dateOfBirth(LocalDate.parse("2000-01-01"))
                .nationality("USA")
                .password("password")
                .build();

        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerRepository.existsByNationalIdNumber(anyString())).thenReturn(false);
        when(customerRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(accountRepository.save(any(Account.class))).thenReturn(new Account());
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            customer.setId(1L);
            return customer;
        });

        CustomerDTO customerDTO = authenticatorService.register(createCustomerDTO);

        assertNotNull(customerDTO);
        assertEquals("test@example.com", customerDTO.getEmail());
        verify(customerRepository, times(1)).existsByEmail(anyString());
        verify(customerRepository, times(1)).existsByNationalIdNumber(anyString());
        verify(customerRepository, times(1)).existsByPhoneNumber(anyString());
        verify(accountRepository, times(1)).save(any(Account.class));
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void register_customerAlreadyExists() {
        CreateCustomerDTO createCustomerDTO = CreateCustomerDTO.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("1234567890")
                .address("123 Main St")
                .gender(MALE)
                .nationalIdNumber("12345678901234")
                .dateOfBirth(LocalDate.parse("2000-01-01"))
                .nationality("USA")
                .password("password")
                .build();

        when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(CustomerAlreadyExistException.class, () -> authenticatorService.register(createCustomerDTO));

        verify(customerRepository, times(1)).existsByEmail(anyString());
        verify(customerRepository, times(0)).existsByNationalIdNumber(anyString());
        verify(customerRepository, times(0)).existsByPhoneNumber(anyString());
        verify(accountRepository, times(0)).save(any(Account.class));
        verify(customerRepository, times(0)).save(any(Customer.class));
    }
}
