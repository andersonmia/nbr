package com.nbr.bankingsystem.services;

import com.nbr.bankingsystem.DTO.CustomerDTO;
import com.nbr.bankingsystem.DTO.CustomerUpdateDTO;
import com.nbr.bankingsystem.models.Customer;

import java.util.List;

/**
 * Interface for customer service operations.
 * This service handles customer-related business logic.
 */
public interface CustomerService {

    /**
     * Creates a new customer.
     *
     * @param customerDTO the data transfer object containing customer details
     * @return the created customer
     */
    Customer createCustomer(CustomerDTO customerDTO);

    /**
     * Updates an existing customer by ID.
     *
     * @param id the ID of the customer to update
     * @param customerUpdateDTO the data transfer object containing updated customer details
     * @return the updated customer
     */
    Customer updateCustomer(Long id, CustomerUpdateDTO customerUpdateDTO);

    /**
     * Retrieves a customer by ID.
     *
     * @param id the ID of the customer to retrieve
     * @return the customer with the given ID
     */
    Customer getCustomerById(Long id);

    /**
     * Deletes a customer by ID.
     *
     * @param id the ID of the customer to delete
     */
    void deleteCustomer(Long id);

    /**
     * Retrieves all customers.
     *
     * @return a list of all customers
     */
    List<Customer> getAllCustomers();

    /**
     * Checks if the specified user is associated with the specified customer.
     *
     * @param customerId the ID of the customer
     * @param userId the ID of the user
     * @return true if the user is associated with the customer, false otherwise
     */
    boolean isCustomerAssociatedWithUser(Long customerId, Long userId);

    /**
     * Finds the user ID associated with the given email.
     *
     * @param email the email of the user
     * @return the ID of the user
     */
    Long findUserIdByEmail(String email);
}
