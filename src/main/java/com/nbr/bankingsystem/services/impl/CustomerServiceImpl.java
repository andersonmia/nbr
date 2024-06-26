package com.nbr.bankingsystem.services.impl;

import com.nbr.bankingsystem.DTO.CustomerDTO;
import com.nbr.bankingsystem.DTO.CustomerUpdateDTO;
import com.nbr.bankingsystem.enums.Role;
import com.nbr.bankingsystem.exceptions.DuplicateEmailException;
import com.nbr.bankingsystem.exceptions.InvalidEmailException;
import com.nbr.bankingsystem.exceptions.InvalidPhoneNumberException;
import com.nbr.bankingsystem.exceptions.ResourceNotFoundException;
import com.nbr.bankingsystem.models.Customer;
import com.nbr.bankingsystem.models.UserModel;
import com.nbr.bankingsystem.repositories.CustomerRepository;
import com.nbr.bankingsystem.repositories.UserRepository;
import com.nbr.bankingsystem.services.CustomerService;
import com.nbr.bankingsystem.services.MessagingService;
import com.nbr.bankingsystem.utils.AuditLogger;
import com.nbr.bankingsystem.utils.DateUtil;
import com.nbr.bankingsystem.utils.ValidationUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessagingService messagingService;

    public CustomerServiceImpl(CustomerRepository customerRepository, UserRepository userRepository,
                               PasswordEncoder passwordEncoder, MessagingService messagingService) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.messagingService = messagingService;
    }

    @Override
    @Transactional
    public Customer createCustomer(CustomerDTO customerDTO) {
        if (!ValidationUtil.isValidMobile(customerDTO.getMobile())) {
            AuditLogger.log("CREATE_CUSTOMER_FAILED", "Invalid mobile number format for mobile: " + customerDTO.getMobile());
            throw new InvalidPhoneNumberException("Invalid mobile number format");
        }

        if (!ValidationUtil.isValidEmail(customerDTO.getEmail())) {
            AuditLogger.log("CREATE_CUSTOMER_FAILED", "Invalid email format: " + customerDTO.getEmail());
            throw new InvalidEmailException("Invalid email format");
        }

        Optional<Customer> existingCustomerByEmail = customerRepository.findByEmail(customerDTO.getEmail());
        if (existingCustomerByEmail.isPresent()) {
            AuditLogger.log("CREATE_CUSTOMER_FAILED", "Email already exists: " + customerDTO.getEmail());
            throw new DuplicateEmailException("Email already exists: " + customerDTO.getEmail());
        }

        Optional<Customer> existingCustomerByMobile = customerRepository.findByMobile(customerDTO.getMobile());
        if (existingCustomerByMobile.isPresent()) {
            AuditLogger.log("CREATE_CUSTOMER_FAILED", "Mobile number already exists: " + customerDTO.getMobile());
            throw new DuplicateEmailException("Mobile number already exists: " + customerDTO.getMobile());
        }

        UserModel userModel = new UserModel();
        userModel.setEmail(customerDTO.getEmail());
        userModel.setPassword(passwordEncoder.encode(customerDTO.getPassword()));
        userModel.setRole(Role.CUSTOMER);
        UserModel savedUserModel = userRepository.save(userModel);

        Customer customer = new Customer();
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setEmail(customerDTO.getEmail());
        customer.setMobile(customerDTO.getMobile());
        customer.setAccount(generateAccountNumber());
        customer.setDob(DateUtil.toDate(customerDTO.getDob()));
        customer.setBalance(0.0);
        customer.setLastUpdateTime(LocalDateTime.now());
        customer.setUserModel(savedUserModel);

        Customer savedCustomer = customerRepository.save(customer);
        AuditLogger.log("CREATE_CUSTOMER", "Created customer with ID: " + savedCustomer.getId());

        String message = String.format("Dear %s %s,\n\nThank you for trusting our bank and creating an account. Your new account number is %s.\n\nBest regards,\nNational Bank of Rwanda",
                customer.getFirstName(), customer.getLastName(), customer.getAccount());
        messagingService.sendTransactionMessage(customer.getEmail(), message, savedCustomer.getId());

        return savedCustomer;
    }

    @Override
    @Transactional
    public Customer updateCustomer(Long id, CustomerUpdateDTO customerUpdateDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    AuditLogger.log("UPDATE_CUSTOMER_FAILED", "Customer not found with id " + id);
                    return new ResourceNotFoundException("Customer not found with id " + id);
                });

        if (customerUpdateDTO.getMobile() != null && !ValidationUtil.isValidMobile(customerUpdateDTO.getMobile())) {
            AuditLogger.log("UPDATE_CUSTOMER_FAILED", "Invalid mobile number format for mobile: " + customerUpdateDTO.getMobile());
            throw new InvalidPhoneNumberException("Invalid mobile number format");
        }

        if (customerUpdateDTO.getEmail() != null && !ValidationUtil.isValidEmail(customerUpdateDTO.getEmail())) {
            AuditLogger.log("UPDATE_CUSTOMER_FAILED", "Invalid email format: " + customerUpdateDTO.getEmail());
            throw new InvalidEmailException("Invalid email format");
        }

        if (customerUpdateDTO.getEmail() != null) {
            Optional<Customer> existingCustomerByEmail = customerRepository.findByEmail(customerUpdateDTO.getEmail());
            if (existingCustomerByEmail.isPresent() && !existingCustomerByEmail.get().getId().equals(id)) {
                AuditLogger.log("UPDATE_CUSTOMER_FAILED", "Email already exists: " + customerUpdateDTO.getEmail());
                throw new DuplicateEmailException("Email already exists: " + customerUpdateDTO.getEmail());
            }
        }

        if (customerUpdateDTO.getMobile() != null) {
            Optional<Customer> existingCustomerByMobile = customerRepository.findByMobile(customerUpdateDTO.getMobile());
            if (existingCustomerByMobile.isPresent() && !existingCustomerByMobile.get().getId().equals(id)) {
                AuditLogger.log("UPDATE_CUSTOMER_FAILED", "Mobile number already exists: " + customerUpdateDTO.getMobile());
                throw new DuplicateEmailException("Mobile number already exists: " + customerUpdateDTO.getMobile());
            }
        }

        if (customerUpdateDTO.getFirstName() != null) {
            customer.setFirstName(customerUpdateDTO.getFirstName());
        }
        if (customerUpdateDTO.getLastName() != null) {
            customer.setLastName(customerUpdateDTO.getLastName());
        }
        if (customerUpdateDTO.getEmail() != null) {
            customer.setEmail(customerUpdateDTO.getEmail());
        }
        if (customerUpdateDTO.getMobile() != null) {
            customer.setMobile(customerUpdateDTO.getMobile());
        }
        if (customerUpdateDTO.getDob() != null) {
            customer.setDob(DateUtil.toDate(customerUpdateDTO.getDob()));
        }
        customer.setLastUpdateTime(LocalDateTime.now());

        Customer updatedCustomer = customerRepository.save(customer);
        AuditLogger.log("UPDATE_CUSTOMER", "Updated customer with ID: " + updatedCustomer.getId());

        return updatedCustomer;
    }


    @Override
    public Customer getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    AuditLogger.log("GET_CUSTOMER_FAILED", "Customer not found with id " + id);
                    return new ResourceNotFoundException("Customer not found with id " + id);
                });
        AuditLogger.log("GET_CUSTOMER", "Fetched customer with ID: " + customer.getId());
        return customer;
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    AuditLogger.log("DELETE_CUSTOMER_FAILED", "Customer not found with id " + id);
                    return new ResourceNotFoundException("Customer not found with id " + id);
                });
        customerRepository.delete(customer);
        AuditLogger.log("DELETE_CUSTOMER", "Deleted customer with ID: " + customer.getId());
    }

    @Override
    public List<Customer> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        AuditLogger.log("GET_ALL_CUSTOMERS", "Fetched all customers");
        return customers;
    }

    @Override
    public boolean isCustomerAssociatedWithUser(Long customerId, Long userId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    AuditLogger.log("CHECK_ASSOCIATION_FAILED", "Customer not found with id " + customerId);
                    return new ResourceNotFoundException("Customer not found with id " + customerId);
                });
        boolean isAssociated = customer.getUserModel().getId().equals(userId);
        AuditLogger.log("CHECK_ASSOCIATION", "Customer association check for customer ID: " + customerId + " and user ID: " + userId + " - Result: " + isAssociated);
        return isAssociated;
    }

    @Override
    public Long findUserIdByEmail(String email) {
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    AuditLogger.log("FIND_USER_ID_FAILED", "User not found with email: " + email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });
        AuditLogger.log("FIND_USER_ID", "Found user ID: " + user.getId() + " for email: " + email);
        return user.getId();
    }

    private String generateAccountNumber() {
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            accountNumber.append(random.nextInt(10));
        }
        return accountNumber.toString();
    }
}
