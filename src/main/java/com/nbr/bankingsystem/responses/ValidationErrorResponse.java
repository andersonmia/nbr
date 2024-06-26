package com.nbr.bankingsystem.responses;

import com.nbr.bankingsystem.enums.ResponseType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ValidationErrorResponse extends Response {

    public ValidationErrorResponse() {
        setResponseType(ResponseType.BAD_REQUEST);
    }
    public ValidationErrorResponse setErrors(List<String> errors) {
        return this;
    }
}
