package org.transferservice.controller;


import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.transferservice.dto.CreateCustomerDTO;
import org.transferservice.exception.custom.CustomerAlreadyExistException;
import org.transferservice.service.security.IAuthenticator;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IAuthenticator authenticator;


    @Test
    void testRegisterUserWithInvalidRequestBody() throws Exception {

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\r\n    \"firstName\": \"Yousef\",\r\n    \"lastName\": \"Doe\",\r\n    \"email\": \"r5x@gmail.com\",\r\n    \"phoneNumber\": \"01050208544\",\r\n    \"address\": \"Egypt\",\r\n    \"nationality\": \"EG\",\r\n    \"nationalIdNumber\": \"12345678911111\",\r\n    \"gender\": \"MALE\",\r\n    \"dateOfBirth\": \"2000-06-30\",\r\n    \"password\": \"123456\",\r\n    \"username\": \"ysrr\"\r\n}"))
                .andExpect(status().isOk());
    }

    @Test
    void testRegisterUserWithValidRequestBody() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@gmail.com\" , \"password\":\"testPassword\" , \"userType\":\"SELLER\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterWithAlreadyExistingUser() throws Exception {

        Mockito.when(this.authenticator.register(any(CreateCustomerDTO.class)))
                .thenThrow(new CustomerAlreadyExistException("User already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"testUser@gmail.com\", \"password\":\"testPassword\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAuthenticate() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"testUser@gmail.com\", \"password\":\"testPassword\"}"))
                .andExpect(status().isOk());
    }

}
