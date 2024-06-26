package com.nbr.bankingsystem.controllers;

import com.nbr.bankingsystem.DTO.BankingDTO;
import com.nbr.bankingsystem.DTO.TransferDTO;
import com.nbr.bankingsystem.models.Banking;
import com.nbr.bankingsystem.responses.Response;
import com.nbr.bankingsystem.enums.ResponseType;
import com.nbr.bankingsystem.services.BankingService;
import com.nbr.bankingsystem.services.CustomerService;
import com.nbr.bankingsystem.utils.ExceptionHandlerUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * The BankingController class handles all the HTTP requests related to banking transactions.
 * It uses the BankingService and CustomerService to perform the necessary operations.
 */
@RestController
@RequestMapping("/banking")
@Tag(name = "Banking System", description = "Operations pertaining to banking transactions in the banking system")
public class BankingController {

    private final BankingService bankingService;
    private final CustomerService customerService;

    /**
     * Constructor for the BankingController class.
     * @param bankingService The service to handle banking operations.
     * @param customerService The service to handle customer operations.
     */
    public BankingController(BankingService bankingService, CustomerService customerService) {
        this.bankingService = bankingService;
        this.customerService = customerService;
    }

    /**
     * Endpoint to create a new transaction.
     * Only accessible to users with the 'CUSTOMER' role.
     * @param bankingDTO The data transfer object containing the transaction details.
     * @param bindingResult The result of the validation of the bankingDTO.
     * @param authentication The authentication object containing the authenticated user's details.
     * @return A ResponseEntity containing the response to the request.
     */
    @PostMapping("/createTransaction")
    @Operation(summary = "Create a new transaction")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Response> createTransaction(@Valid @RequestBody BankingDTO bankingDTO, BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new Response()
                    .setResponseType(ResponseType.BAD_REQUEST)
                    .setPayload(bindingResult.getFieldErrors()));
        }

        try {
            String email = authentication.getName();
            return ResponseEntity.status(201)
                    .body(new Response()
                            .setResponseType(ResponseType.SUCCESS)
                            .setPayload(bankingService.createTransaction(email, bankingDTO)));
        } catch (Exception e) {
            return ExceptionHandlerUtil.handleException(e);
        }
    }

    /**
     * Endpoint to get all transactions.
     * Only accessible to users with the 'ADMIN' role.
     * @return A ResponseEntity containing the response to the request.
     */
    @GetMapping("/")
    @Operation(summary = "Get all transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> getAllTransactions() {
        try {
            List<Banking> transactions = bankingService.getAllTransactions();
            return ResponseEntity.status(200)
                    .body(new Response()
                            .setResponseType(ResponseType.SUCCESS)
                            .setPayload(transactions));
        } catch (Exception e) {
            return ExceptionHandlerUtil.handleException(e);
        }
    }


    /**
     * Endpoint to get a transaction by its ID.
     * Accessible to users with the 'ADMIN' role and to 'CUSTOMER' users who own the transaction.
     * @param id The ID of the transaction.
     * @param authentication The authentication object containing the authenticated user's details.
     * @return A ResponseEntity containing the response to the request.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a transaction by ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<Response> getTransactionById(@PathVariable Long id, Authentication authentication) {
        try {
            if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                if (!bankingService.isTransactionOwner(authentication, id)) {
                    return ResponseEntity.status(403).body(new Response()
                            .setResponseType(ResponseType.FORBIDDEN)
                            .setMessage("You have no permissions to access this endpoint."));
                }
            }
            return ResponseEntity.status(200)
                    .body(new Response()
                            .setResponseType(ResponseType.SUCCESS)
                            .setPayload(bankingService.getTransactionById(id)));
        } catch (Exception e) {
            return ExceptionHandlerUtil.handleException(e);
        }
    }

    /**
     * Endpoint to transfer money.
     * Only accessible to users with the 'CUSTOMER' role.
     * @param transferDTO The data transfer object containing the transfer details.
     * @param bindingResult The result of the validation of the transferDTO.
     * @param authentication The authentication object containing the authenticated user's details.
     * @return A ResponseEntity containing the response to the request.
     */
    @PostMapping("/transfer")
    @Operation(summary = "Transfer money")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Response> transfer(@Valid @RequestBody TransferDTO transferDTO, BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new Response()
                    .setResponseType(ResponseType.BAD_REQUEST)
                            .setMessage(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage()))
            ;
        }

        try {
            String email = authentication.getName();
            return ResponseEntity.status(201)
                    .body(new Response()
                            .setResponseType(ResponseType.SUCCESS)
                            .setPayload(bankingService.transfer(email, transferDTO)));
        } catch (Exception e) {

            return ExceptionHandlerUtil.handleException(e);
        }
    }

    /**
     * Endpoint to get the balance for the authenticated customer.
     * Only accessible to users with the 'CUSTOMER' role.
     * @param authentication The authentication object containing the authenticated user's details.
     * @return A ResponseEntity containing the response to the request.
     */
    @GetMapping("/balance")
    @Operation(summary = "Get balance for authenticated customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Response> getBalance(Authentication authentication) {
        try {
            String email = authentication.getName();
            Long userId = customerService.findUserIdByEmail(email);
            return ResponseEntity.status(200)
                    .body(new Response()
                            .setMessage("Balance retrieved successfully")
                            .setResponseType(ResponseType.SUCCESS)
                            .setPayload(bankingService.getBalance(userId)));
        } catch (Exception e) {
            return ExceptionHandlerUtil.handleException(e);
        }
    }
}
