package com.nbr.bankingsystem.exceptions;

/**
 * Exception thrown when an invalid email format is encountered.
 */
public class InvalidEmailException extends RuntimeException {
    /**
     * Constructs a new InvalidEmailException with the specified detail message.
     * @param message the detail message
     */
    public InvalidEmailException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidEmailException with the specified detail message and cause.
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public InvalidEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
