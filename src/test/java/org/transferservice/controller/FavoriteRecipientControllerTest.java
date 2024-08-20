package org.transferservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.transferservice.dto.FavoriteRecipientDTO;
import org.transferservice.model.FavoriteRecipient;
import org.transferservice.service.FavoriteRecipientService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class FavoriteRecipientControllerTest {

    @Mock
    private FavoriteRecipientService favoriteRecipientService;

    @InjectMocks
    private FavoriteRecipientController favoriteRecipientController;

    private FavoriteRecipient favoriteRecipient;
    private FavoriteRecipientDTO favoriteRecipientDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        favoriteRecipientDTO = FavoriteRecipientDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .recipientAccountNumber("123456789")
                .build();

        favoriteRecipient = FavoriteRecipient.builder()
                .id(1L)
                .recipientName("John Doe")
                .recipientAccountNumber("123456789")
                .build();
    }

    @Test
    void addFavoriteRecipient_success() {
        when(favoriteRecipientService.addFavoriteRecipient(any(FavoriteRecipientDTO.class), anyLong())).thenReturn(favoriteRecipient);

        ResponseEntity<FavoriteRecipient> response = favoriteRecipientController.addFavoriteRecipient(1L, favoriteRecipientDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getRecipientName());
    }

    @Test
    void getFavoriteRecipientsByCustomerId_success() {
        List<FavoriteRecipientDTO> favoriteRecipients = new ArrayList<>();
        favoriteRecipients.add(favoriteRecipientDTO);
        when(favoriteRecipientService.getFavoriteRecipientsByCustomerId(1L)).thenReturn(favoriteRecipients);

        ResponseEntity<List<FavoriteRecipientDTO>> response = favoriteRecipientController.getFavoriteRecipientsByCustomerId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("John", response.getBody().get(0).getFirstName());
    }

    @Test
    void getFavoriteRecipientByCustomerIdAndAccountNumber_success() {
        when(favoriteRecipientService.getFavoriteRecipientByCustomerIdAndAccountNumber(1L, "123456789")).thenReturn(Optional.of(favoriteRecipient));

        ResponseEntity<FavoriteRecipient> response = favoriteRecipientController.getFavoriteRecipientByCustomerIdAndAccountNumber(1L, "123456789");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getRecipientName());
    }

    @Test
    void getFavoriteRecipientByCustomerIdAndAccountNumber_notFound() {
        when(favoriteRecipientService.getFavoriteRecipientByCustomerIdAndAccountNumber(1L, "123456789")).thenReturn(Optional.empty());

        ResponseEntity<FavoriteRecipient> response = favoriteRecipientController.getFavoriteRecipientByCustomerIdAndAccountNumber(1L, "123456789");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void removeFavoriteRecipient_success() {
        doNothing().when(favoriteRecipientService).removeFavoriteRecipient(1L);

        ResponseEntity<Void> response = favoriteRecipientController.removeFavoriteRecipient(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(favoriteRecipientService, times(1)).removeFavoriteRecipient(1L);
    }




}
