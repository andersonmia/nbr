package com.nbr.bankingsystem.services;

import com.nbr.bankingsystem.DTO.AuthenticationRequest;
import com.nbr.bankingsystem.DTO.AuthenticationResponse;;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {

    public AuthenticationResponse authenticate(AuthenticationRequest request) ;
}
