package com.nbr.bankingsystem.services;

import com.nbr.bankingsystem.DTO.BankingDTO;
import com.nbr.bankingsystem.DTO.TransferDTO;
import com.nbr.bankingsystem.models.Banking;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Interface for banking service operations.
 * This service handles banking-related business logic.
 */
public interface BankingService {
    /**
     * Creates a new transaction for the given customer.
     *
     * @param customerEmail the email of the customer performing the transaction
     * @param bankingDTO    the data transfer object containing transaction details
     * @return the created banking transaction
     */
    Banking createTransaction(String customerEmail, BankingDTO bankingDTO);

    /**
     * Retrieves all transactions.
     *
     * @return a list of all banking transactions
     */
    List<Banking> getAllTransactions();

    /**
     * Retrieves a transaction by ID.
     *
     * @param id the ID of the transaction to retrieve
     * @return the banking transaction with the given ID
     */
    Banking getTransactionById(Long id);

    /**
     * Transfers money from one customer to another.
     *
     * @param senderEmail  the email of the customer sending the money
     * @param transferDTO  the data transfer object containing transfer details
     * @return the created transfer transaction
     */
    Banking transfer(String senderEmail, TransferDTO transferDTO);

    /**
     * Checks if the authenticated user owns the specified transaction.
     *
     * @param authentication the authentication object of the user
     * @param transactionId  the ID of the transaction to check
     * @return true if the user owns the transaction, false otherwise
     */
    boolean isTransactionOwner(Authentication authentication, Long transactionId);

    /**
     * Retrieves the balance for the given customer.
     * @param customerId the ID of the customer
     * @return the balance of the customer
     */
    double getBalance(Long customerId);
}
