package com.nbr.bankingsystem.services.impl;

import com.nbr.bankingsystem.exceptions.ResourceNotFoundException;
import com.nbr.bankingsystem.models.Customer;
import com.nbr.bankingsystem.models.Message;
import com.nbr.bankingsystem.repositories.CustomerRepository;
import com.nbr.bankingsystem.repositories.MessageRepository;
import com.nbr.bankingsystem.services.MessagingService;
import com.nbr.bankingsystem.utils.AuditLogger;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class MessagingServiceImpl implements MessagingService {

    private final JavaMailSender mailSender;
    private final MessageRepository messageRepository;
    private final CustomerRepository customerRepository;

    public MessagingServiceImpl(JavaMailSender mailSender, MessageRepository messageRepository, CustomerRepository customerRepository) {
        this.mailSender = mailSender;
        this.messageRepository = messageRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public void sendTransactionMessage(String email, String message, Long customerId) {
        // Fetch customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    AuditLogger.log("SEND_MESSAGE_FAILED", "Customer not found with id: " + customerId);
                    return new ResourceNotFoundException("Customer not found with id: " + customerId);
                });

        // Format the date-time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        // Create email content
        String emailContent = String.format(
                """
                        Dear %s %s,

                        Your saving of %.2f on your account %s has been completed at %s successfully.

                        Best regards,
                        National Bank of Rwanda""",
                customer.getFirstName(), customer.getLastName(),
                customer.getBalance(), customer.getAccount(), formattedDateTime
        );

        // Send email
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("National Bank of Rwanda - Account Transactions");
        mailMessage.setText(emailContent);
        mailSender.send(mailMessage);
        AuditLogger.log("SEND_EMAIL", "Sent email to: " + email);

        // Log message in Message table
        Message messageEntity = new Message();
        messageEntity.setCustomer(customer);
        messageEntity.setMessage(emailContent);
        messageEntity.setMessageDateTime(now);
        messageRepository.save(messageEntity);
        AuditLogger.log("LOG_MESSAGE", "Logged message for customer ID: " + customerId);
    }
}
