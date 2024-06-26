package com.nbr.bankingsystem.services.impl;

import com.nbr.bankingsystem.DTO.BankingDTO;
import com.nbr.bankingsystem.DTO.TransferDTO;
import com.nbr.bankingsystem.enums.TransactionType;
import com.nbr.bankingsystem.exceptions.InsufficientBalanceException;
import com.nbr.bankingsystem.exceptions.InvalidTransactionTypeException;
import com.nbr.bankingsystem.exceptions.ResourceNotFoundException;
import com.nbr.bankingsystem.models.Banking;
import com.nbr.bankingsystem.models.Customer;
import com.nbr.bankingsystem.repositories.BankingRepository;
import com.nbr.bankingsystem.repositories.CustomerRepository;
import com.nbr.bankingsystem.repositories.UserRepository;
import com.nbr.bankingsystem.services.BankingService;
import com.nbr.bankingsystem.services.MessagingService;
import com.nbr.bankingsystem.utils.AuditLogger;
import com.nbr.bankingsystem.utils.EnumConverter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BankingServiceImpl implements BankingService {

    private final BankingRepository bankingRepository;
    private final CustomerRepository customerRepository;
    private final MessagingService messagingService;
    private final UserRepository userRepository;

    public BankingServiceImpl(BankingRepository bankingRepository, CustomerRepository customerRepository,
                              MessagingService messagingService, UserRepository userRepository) {
        this.bankingRepository = bankingRepository;
        this.customerRepository = customerRepository;
        this.messagingService = messagingService;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Banking createTransaction(String customerEmail, BankingDTO bankingDTO) {
        if (bankingDTO.getAmount() <= 0) {
            AuditLogger.log("CREATE_TRANSACTION_FAILED", "Transaction amount must be greater than zero for customer email: " + customerEmail);
            throw new InvalidTransactionTypeException("Transaction amount must be greater than zero");
        }

        Customer customer = customerRepository.findByEmail(customerEmail)
                .orElseThrow(() -> {
                    AuditLogger.log("CREATE_TRANSACTION_FAILED", "Customer not found with email: " + customerEmail);
                    return new ResourceNotFoundException("Customer not found with email: " + customerEmail);
                });

        Banking banking = new Banking();
        banking.setCustomer(customer);
        banking.setAccount(customer.getAccount());
        banking.setAmount(bankingDTO.getAmount());
        banking.setType(EnumConverter.toTransactionType(bankingDTO.getType()));
        banking.setBankingDateTime(LocalDateTime.now());

        if (banking.getType() == TransactionType.SAVING) {
            customer.setBalance(customer.getBalance() + banking.getAmount());
        } else if (banking.getType() == TransactionType.WITHDRAW) {
            if (customer.getBalance() < banking.getAmount()) {
                AuditLogger.log("CREATE_TRANSACTION_FAILED", "Insufficient balance for withdrawal for customer email: " + customerEmail);
                throw new InsufficientBalanceException("Insufficient balance for withdrawal");
            }
            customer.setBalance(customer.getBalance() - banking.getAmount());
        } else {
            AuditLogger.log("CREATE_TRANSACTION_FAILED", "Invalid transaction type: " + bankingDTO.getType() + " for customer email: " + customerEmail);
            throw new InvalidTransactionTypeException("Invalid transaction type: " + bankingDTO.getType());
        }

        customerRepository.save(customer);
        Banking savedBanking = bankingRepository.save(banking);

        String message = String.format("Dear %s %s, your %s of %.2f on your account %s has been completed at %s successfully.",
                customer.getFirstName(), customer.getLastName(), banking.getType().toString().toLowerCase(),
                banking.getAmount(), banking.getAccount(), banking.getBankingDateTime().toString());

        // Send transaction message to customer
        messagingService.sendTransactionMessage(customer.getEmail(), message, customer.getId());

        AuditLogger.log("CREATE_TRANSACTION", "Created transaction with ID: " + savedBanking.getId() + " for customer email: " + customerEmail);
        return savedBanking;
    }

    @Override
    public List<Banking> getAllTransactions() {
        List<Banking> transactions = bankingRepository.findAll();
        AuditLogger.log("GET_ALL_TRANSACTIONS", "Fetched all transactions");
        return transactions;
    }

    @Override
    public Banking getTransactionById(Long id) {
        Banking transaction = bankingRepository.findById(id)
                .orElseThrow(() -> {
                    AuditLogger.log("GET_TRANSACTION_FAILED", "Transaction not found with id " + id);
                    return new ResourceNotFoundException("Transaction not found with id " + id);
                });
        AuditLogger.log("GET_TRANSACTION", "Fetched transaction with ID: " + transaction.getId());
        return transaction;
    }

    @Override
    @Transactional
    public Banking transfer(String senderEmail, TransferDTO transferDTO) {
        if (transferDTO.getAmount() <= 0) {
            AuditLogger.log("TRANSFER_FAILED", "Transfer amount must be greater than zero for sender email: " + senderEmail);
            throw new InvalidTransactionTypeException("Transfer amount must be greater than zero");
        }

        Customer sender = customerRepository.findByEmail(senderEmail)
                .orElseThrow(() -> {
                    AuditLogger.log("TRANSFER_FAILED", "Customer not found with email: " + senderEmail);
                    return new ResourceNotFoundException("Customer not found with email: " + senderEmail);
                });
        Customer receiver = customerRepository.findById(transferDTO.getReceiverCustomerId())
                .orElseThrow(() -> {
                    AuditLogger.log("TRANSFER_FAILED", "Receiver not found with ID: " + transferDTO.getReceiverCustomerId());
                    return new ResourceNotFoundException("Receiver not found with ID: " + transferDTO.getReceiverCustomerId());
                });

        if (sender.getId().equals(receiver.getId())) {
            AuditLogger.log("TRANSFER_FAILED", "Cannot transfer money to the same account for sender email: " + senderEmail);
            throw new InvalidTransactionTypeException("Cannot transfer money to the same account");
        }

        if (sender.getBalance() < transferDTO.getAmount()) {
            AuditLogger.log("TRANSFER_FAILED", "Insufficient balance for transfer for sender email: " + senderEmail);
            throw new InsufficientBalanceException("Insufficient balance for transfer");
        }

        sender.setBalance(sender.getBalance() - transferDTO.getAmount());
        receiver.setBalance(receiver.getBalance() + transferDTO.getAmount());

        customerRepository.save(sender);
        customerRepository.save(receiver);

        Banking transfer = new Banking();
        transfer.setCustomer(sender);
        transfer.setAccount(sender.getAccount());
        transfer.setAmount(transferDTO.getAmount());
        transfer.setType(TransactionType.TRANSFER);
        transfer.setBankingDateTime(LocalDateTime.now());

        Banking savedTransfer = bankingRepository.save(transfer);

        String senderMessage = String.format("Dear %s %s, your transfer of %.2f to account %s has been completed at %s successfully.",
                sender.getFirstName(), sender.getLastName(), transfer.getAmount(),
                receiver.getAccount(), transfer.getBankingDateTime().toString());
        messagingService.sendTransactionMessage(sender.getEmail(), senderMessage, sender.getId());

        String receiverMessage = String.format("Dear %s %s, you have received a transfer of %.2f from account %s at %s.",
                receiver.getFirstName(), receiver.getLastName(), transfer.getAmount(),
                sender.getAccount(), transfer.getBankingDateTime().toString());
        messagingService.sendTransactionMessage(receiver.getEmail(), receiverMessage, receiver.getId());

        AuditLogger.log("TRANSFER", "Transfer transaction created with ID: " + savedTransfer.getId() + " from sender email: " + senderEmail + " to receiver ID: " + transferDTO.getReceiverCustomerId());
        return savedTransfer;
    }

    @Override
    public boolean isTransactionOwner(Authentication authentication, Long transactionId) {
        Banking transaction = getTransactionById(transactionId);
        String customerEmail = transaction.getCustomer().getEmail();
        boolean isOwner = authentication.getName().equals(customerEmail);
        AuditLogger.log("CHECK_TRANSACTION_OWNER", "Transaction ownership check for transaction ID: " + transactionId + " by user: " + authentication.getName() + " - Result: " + isOwner);
        return isOwner;
    }

    @Override
    public double getBalance(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    AuditLogger.log("GET_BALANCE_FAILED", "Customer not found with ID: " + customerId);
                    return new ResourceNotFoundException("Customer not found with ID: " + customerId);
                });
        double balance = customer.getBalance();
        AuditLogger.log("GET_BALANCE", "Fetched balance for customer ID: " + customerId + " - Balance: " + balance);
        return balance;
    }
}
