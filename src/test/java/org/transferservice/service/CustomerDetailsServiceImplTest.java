package org.transferservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.transferservice.model.Customer;
import org.transferservice.repository.CustomerRepository;
import org.transferservice.service.security.CustomerDetailsServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomerDetailsServiceImplTest {
    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerDetailsServiceImpl customerDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_existingUser() {
        String email = "test@example.com";
        Customer customer = Customer.builder()
                .email(email)
                .password("password")
                .build();

        when(customerRepository.findUserByEmail(email)).thenReturn(Optional.of(customer));

        UserDetails userDetails = customerDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        verify(customerRepository, times(1)).findUserByEmail(email);
    }

    @Test
    void loadUserByUsername_userNotFound() {
        String email = "notfound@example.com";

        when(customerRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            customerDetailsService.loadUserByUsername(email);
        });

        assertEquals("Customer Not Found with email: " + email, exception.getMessage());
        verify(customerRepository, times(1)).findUserByEmail(email);
    }
}
