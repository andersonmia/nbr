package com.nbr.bankingsystem.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
    public DuplicateEmailException(String resource, String field, Object value) {
        super(String.format("%s already exists with %s : '%s'", resource, field, value));
    }
}
