package com.nbr.bankingsystem.services;

public interface MessagingService {
    void sendTransactionMessage(String email, String message, Long customerId);
}
