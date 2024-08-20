package org.transferservice.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.transferservice.dto.CustomerDTO;
import org.transferservice.dto.UpdateCustomerDTO;
import org.transferservice.exception.custom.CustomerNotFoundException;
import org.transferservice.model.Customer;
import org.transferservice.repository.CustomerRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private UpdateCustomerDTO updateCustomerDTO;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .firstName("Alex")
                .lastName("Johnson")
                .email("alex.johnson@example.com")
                .phoneNumber("+1234567890")
                .address("123 Elm St, Newtown, USA")
                .nationality("American")
                .nationalIdNumber("12345678901234")
                .dateOfBirth(LocalDate.of(1988, 5, 15))
                .build();

        updateCustomerDTO = UpdateCustomerDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+0987654321")
                .address("456 Maple St, Othertown, USA")
                .nationality("American")
                .nationalIdNumber("98765432109876")
                .dateOfBirth(LocalDate.of(1990, 3, 25))
                .build();
    }

    @Test
    void updateCustomer_success() throws CustomerNotFoundException {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer updatedCustomer = customerService.updateCustomer(1L, updateCustomerDTO);

        assertNotNull(updatedCustomer);
        assertEquals("John", updatedCustomer.getFirstName());
        assertEquals("Doe", updatedCustomer.getLastName());
        assertEquals("john.doe@example.com", updatedCustomer.getEmail());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void updateCustomer_customerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class, () -> {
            customerService.updateCustomer(1L, updateCustomerDTO);
        });

        assertEquals("Customer with Id 1 not found", exception.getMessage());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_success() throws CustomerNotFoundException {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        doNothing().when(customerRepository).delete(any(Customer.class));

        customerService.deleteCustomer(1L);

        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).delete(any(Customer.class));
    }

    @Test
    void deleteCustomer_customerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class, () -> {
            customerService.deleteCustomer(1L);
        });

        assertEquals("Customer with Id 1 not found", exception.getMessage());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, never()).delete(any(Customer.class));
    }

    @Test
    void getCustomerById_success() throws CustomerNotFoundException {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerDTO customerDTO = customerService.getCustomerById(1L);

        assertNotNull(customerDTO);
        assertEquals("Alex", customerDTO.getFirstName());
        assertEquals("Johnson", customerDTO.getLastName());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void getCustomerById_customerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getCustomerById(1L);
        });

        assertEquals("Customer with Id 1 not found", exception.getMessage());
        verify(customerRepository, times(1)).findById(1L);
    }



}
