package com.nbr.bankingsystem.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthenticationResponse {
    private String token;

    public AuthenticationResponse(String token) {
        this.token = token;
    }

}
