package com.nbr.bankingsystem.utils;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.nbr.bankingsystem.enums.ResponseType;
import com.nbr.bankingsystem.exceptions.*;
import com.nbr.bankingsystem.responses.Response;

@RestControllerAdvice //global exception handler
//Response Entity is a generic container for returning data along with an HTTP status code
public class ExceptionHandlerUtil {

    /**
     * Handles all exceptions and maps them to appropriate HTTP responses.
     *
     * @param e the exception thrown
     * @return a ResponseEntity with a custom Response object and HTTP status code
     */
    @ExceptionHandler(Exception.class) //handles all exceptions of type Exception
    public static ResponseEntity<Response> handleException(Exception e) {
        if (e instanceof ResourceNotFoundException) {
            return ResponseEntity.status(404)
                    .body(new Response()
                            .setResponseType(ResponseType.RESOURCE_NOT_FOUND)
                            .setMessage(e.getMessage())
                            .setPayload(null));
        } else if (e instanceof DuplicateEmailException) {
            return ResponseEntity.status(409)
                    .body(new Response()
                            .setResponseType(ResponseType.DUPLICATE_EMAIL)
                            .setMessage(e.getMessage())
                            .setPayload(null));
        } else if (e instanceof InsufficientBalanceException) {
            return ResponseEntity.status(400)
                    .body(new Response()
                            .setResponseType(ResponseType.INSUFFICIENT_BALANCE)
                            .setMessage(e.getMessage())
                            .setPayload(null));
        } else if (e instanceof InvalidTransactionTypeException) {
            return ResponseEntity.status(400)
                    .body(new Response()
                            .setResponseType(ResponseType.INVALID_TRANSACTION_TYPE)
                            .setMessage(e.getMessage())
                            .setPayload(null));
        } else {
            return ResponseEntity.status(500)
                    .body(new Response()
                            .setResponseType(ResponseType.INTERNAL_SERVER_ERROR)
                            .setMessage(e.getMessage())
                            .setPayload(null));
        }
    }
}
