package org.transferservice.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.transferservice.dto.CreateCustomerDTO;
import org.transferservice.dto.CustomerDTO;
import org.transferservice.dto.LoginRequestDTO;
import org.transferservice.dto.LoginResponseDTO;
import org.transferservice.exception.custom.CustomerAlreadyExistException;
import org.transferservice.exception.response.ErrorDetails;
import org.transferservice.service.security.IAuthenticator;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/authentication")
@Validated
@Tag(name = "Customer Auth Controller", description = "Customer Auth controller")
public class AuthController {

    private final IAuthenticator authenticatorService;

    @Operation(summary = "Register new Customer")
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = CustomerDTO.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ErrorDetails.class), mediaType = "application/json")})
    @PostMapping("/register")
    public CustomerDTO register(@RequestBody @Valid CreateCustomerDTO createCustomerDTO) throws CustomerAlreadyExistException {
        return this.authenticatorService.register(createCustomerDTO);
    }

    @Operation(summary = "Login and generate JWT")
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = LoginResponseDTO.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = ErrorDetails.class), mediaType = "application/json")})
    @PostMapping("/login")
    public LoginResponseDTO longin(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        return this.authenticatorService.login(loginRequestDTO);
    }
}
