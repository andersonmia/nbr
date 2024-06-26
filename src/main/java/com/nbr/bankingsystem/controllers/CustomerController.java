package com.nbr.bankingsystem.controllers;

import com.nbr.bankingsystem.DTO.CustomerDTO;
import com.nbr.bankingsystem.DTO.CustomerUpdateDTO;
import com.nbr.bankingsystem.models.Customer;
import com.nbr.bankingsystem.responses.Response;
import com.nbr.bankingsystem.enums.ResponseType;
import com.nbr.bankingsystem.services.CustomerService;
import com.nbr.bankingsystem.utils.ExceptionHandlerUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Objects;

@RestController
@RequestMapping("/customers")
@Tag(name = "Customer Management System", description = "Operations pertaining to customers in the banking system")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new Customer")
    public ResponseEntity<Response> registerCustomer(@Valid @RequestBody CustomerDTO customerDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new Response()
                    .setResponseType(ResponseType.BAD_REQUEST)
                    .setMessage(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage()));
        }

        try {
            Customer customer = customerService.createCustomer(customerDTO);
            return ResponseEntity.status(201)
                    .body(new Response()
                            .setResponseType(ResponseType.SUCCESS)
                            .setPayload(customer));
        } catch (Exception e) {
            return ExceptionHandlerUtil.handleException(e);
        }
    }

    @GetMapping("/")
    @Operation(summary = "Get all Customers")
    public ResponseEntity<Response> getAllCustomers(Authentication authentication) {
        try {
            if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(403).body(new Response()
                        .setResponseType(ResponseType.FORBIDDEN)
                        .setMessage("You have no permissions to access this endpoint."));
            }

            return ResponseEntity.status(200)
                    .body(new Response()
                            .setResponseType(ResponseType.SUCCESS)
                            .setPayload(customerService.getAllCustomers()));
        } catch (Exception e) {
            return ExceptionHandlerUtil.handleException(e);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a Customer by ID")
    public ResponseEntity<Response> getCustomerById(@PathVariable Long id, Authentication authentication) {
        try {
            Long userId = customerService.findUserIdByEmail(authentication.getName());
            if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                if (!customerService.isCustomerAssociatedWithUser(id, userId)) {
                    return ResponseEntity.status(403).body(new Response()
                            .setResponseType(ResponseType.FORBIDDEN)
                            .setMessage("You have no permissions to access this endpoint."));
                }
            }

            return ResponseEntity.status(200)
                    .body(new Response()
                            .setResponseType(ResponseType.SUCCESS)
                            .setPayload(customerService.getCustomerById(id)));
        } catch (Exception e) {
            return ExceptionHandlerUtil.handleException(e);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Customer by ID")
    public ResponseEntity<Response> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerUpdateDTO customerDetails, BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new Response()
                    .setResponseType(ResponseType.BAD_REQUEST)
                    .setMessage(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage()));
        }

        try {
            Long userId = customerService.findUserIdByEmail(authentication.getName());
            if (!customerService.isCustomerAssociatedWithUser(id, userId)) {
                return ResponseEntity.status(403).body(new Response()
                        .setResponseType(ResponseType.FORBIDDEN)
                        .setMessage("You have no permissions to access this endpoint."));
            }

            return ResponseEntity.status(200)
                    .body(new Response()
                            .setResponseType(ResponseType.SUCCESS)
                            .setPayload(customerService.updateCustomer(id, customerDetails)));
        } catch (Exception e) {
            return ExceptionHandlerUtil.handleException(e);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Customer by ID")
    public ResponseEntity<Response> deleteCustomer(@PathVariable Long id, Authentication authentication) {
        try {
            Long userId = customerService.findUserIdByEmail(authentication.getName());
            if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                if (!customerService.isCustomerAssociatedWithUser(id, userId)) {
                    return ResponseEntity.status(403).body(new Response()
                            .setResponseType(ResponseType.FORBIDDEN)
                            .setMessage("You have no permissions to access this endpoint."));
                }
            }

            customerService.deleteCustomer(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ExceptionHandlerUtil.handleException(e);
        }
    }
}
