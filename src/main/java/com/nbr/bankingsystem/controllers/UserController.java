package com.nbr.bankingsystem.controllers;

import com.nbr.bankingsystem.DTO.AuthenticationRequest;
import com.nbr.bankingsystem.DTO.AuthenticationResponse;
import com.nbr.bankingsystem.enums.Role;
import com.nbr.bankingsystem.models.UserModel;
import com.nbr.bankingsystem.responses.Response;
import com.nbr.bankingsystem.responses.ValidationErrorResponse;
import com.nbr.bankingsystem.services.UserService;
import com.nbr.bankingsystem.services.AuthenticationService;
import com.nbr.bankingsystem.utils.ExceptionHandlerUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Tag(name = "User Management System", description = "Operations pertaining to users in the banking system")
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public UserController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new User")
    public ResponseEntity<Response> registerUser(@Valid @RequestBody AuthenticationRequest authenticationRequest,
                                                 @Parameter(
                                                         name = "role",
                                                         required = true,
                                                         in = ParameterIn.QUERY,
                                                         schema = @Schema(type = "string", allowableValues = {"ADMIN", "TELLER"})
                                                 )@RequestParam Role role,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());

            ValidationErrorResponse errorResponse = new ValidationErrorResponse()
                    .setMessage(errorMessages.toString())
                    .setErrors(errorMessages);

            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            UserModel user = userService.registerUser(authenticationRequest, role);
            return ResponseEntity.ok(
                    new Response("User registered successfully",user)
            );
        } catch (Exception e) {
            return ExceptionHandlerUtil.handleException(e);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate a User")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new AuthenticationResponse(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage()));
        }

        try {
            AuthenticationResponse response = authenticationService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new AuthenticationResponse(e.getMessage()));
        }
    }
}
