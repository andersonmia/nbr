package com.nbr.bankingsystem.responses;

import com.nbr.bankingsystem.enums.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
@NoArgsConstructor
@Accessors(chain = true)
@AllArgsConstructor
@Getter
public class Response {
    private ResponseType responseType;
    @Getter
    private String message;
    private Object payload;
    private List<String> errors;
    public Response(String message, Object payload) {
        this.message = message;
        this.payload = payload;
    }

    public Response setResponseType(ResponseType responseType) {
        this.responseType = responseType;
        return this;
    }

    public Response setMessage(String message) {
        this.message = message;
        return this;
    }

    public Response setPayload(Object payload) {
        this.payload = payload;
        return this;
    }

    public ValidationErrorResponse setErrors(List<String> errors) {
        this.errors = errors;
        return (ValidationErrorResponse) this;
    }
}
