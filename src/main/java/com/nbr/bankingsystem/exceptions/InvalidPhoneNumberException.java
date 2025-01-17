package com.nbr.bankingsystem.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPhoneNumberException extends RuntimeException {
    public InvalidPhoneNumberException(String message) {
        super(message);
    }
}
