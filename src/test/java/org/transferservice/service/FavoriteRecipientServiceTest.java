package org.transferservice.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.transferservice.dto.FavoriteRecipientDTO;
import org.transferservice.model.Customer;
import org.transferservice.model.FavoriteRecipient;
import org.transferservice.repository.CustomerRepository;
import org.transferservice.repository.FavoriteRecipientRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FavoriteRecipientServiceTest {

    @Mock
    private FavoriteRecipientRepository favoriteRecipientRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private FavoriteRecipientService favoriteRecipientService;

    private FavoriteRecipientDTO favoriteRecipientDTO;
    private Customer customer;
    private FavoriteRecipient favoriteRecipient;

    @BeforeEach
    void setUp() {
        favoriteRecipientDTO = FavoriteRecipientDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .recipientAccountNumber("1234567890")
                .build();

        customer = Customer.builder()
                .id(1L)
                .firstName("Alex")
                .lastName("Johnson")
                .email("alex.johnson@example.com")
                .build();

        favoriteRecipient = FavoriteRecipient.builder()
                .recipientName("John Doe")
                .recipientAccountNumber("1234567890")
                .customer(customer)
                .build();
    }

    @Test
    void addFavoriteRecipient_success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(favoriteRecipientRepository.save(any(FavoriteRecipient.class))).thenReturn(favoriteRecipient);

        FavoriteRecipient createdFavoriteRecipient = favoriteRecipientService.addFavoriteRecipient(favoriteRecipientDTO, 1L);

        assertNotNull(createdFavoriteRecipient);
        assertEquals("John Doe", createdFavoriteRecipient.getRecipientName());
        assertEquals("1234567890", createdFavoriteRecipient.getRecipientAccountNumber());
        verify(customerRepository, times(1)).findById(1L);
        verify(favoriteRecipientRepository, times(1)).save(any(FavoriteRecipient.class));
    }

    @Test
    void addFavoriteRecipient_customerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            favoriteRecipientService.addFavoriteRecipient(favoriteRecipientDTO, 1L);
        });

        assertEquals("Customer not found", exception.getMessage());
        verify(customerRepository, times(1)).findById(1L);
        verify(favoriteRecipientRepository, never()).save(any(FavoriteRecipient.class));
    }

    @Test
    void getFavoriteRecipientsByCustomerId_success() {
        when(favoriteRecipientRepository.findByCustomerId(1L)).thenReturn(Arrays.asList(favoriteRecipient));

        List<FavoriteRecipientDTO> favoriteRecipientDTOList = favoriteRecipientService.getFavoriteRecipientsByCustomerId(1L);

        assertNotNull(favoriteRecipientDTOList);
        assertEquals(1, favoriteRecipientDTOList.size());
        assertEquals("John", favoriteRecipientDTOList.get(0).getFirstName());
        assertEquals("Doe", favoriteRecipientDTOList.get(0).getLastName());
        assertEquals("1234567890", favoriteRecipientDTOList.get(0).getRecipientAccountNumber());
        verify(favoriteRecipientRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    void getFavoriteRecipientByCustomerIdAndAccountNumber_success() {
        when(favoriteRecipientRepository.findByCustomerIdAndRecipientAccountNumber(1L, "1234567890"))
                .thenReturn(Optional.of(favoriteRecipient));

        Optional<FavoriteRecipient> foundRecipient = favoriteRecipientService
                .getFavoriteRecipientByCustomerIdAndAccountNumber(1L, "1234567890");

        assertTrue(foundRecipient.isPresent());
        assertEquals("John Doe", foundRecipient.get().getRecipientName());
        assertEquals("1234567890", foundRecipient.get().getRecipientAccountNumber());
        verify(favoriteRecipientRepository, times(1))
                .findByCustomerIdAndRecipientAccountNumber(1L, "1234567890");
    }

    @Test
    void removeFavoriteRecipient_success() {
        doNothing().when(favoriteRecipientRepository).deleteById(1L);

        favoriteRecipientService.removeFavoriteRecipient(1L);

        verify(favoriteRecipientRepository, times(1)).deleteById(1L);
    }


}
