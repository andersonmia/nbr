package com.nbr.bankingsystem.services.impl;

import com.nbr.bankingsystem.DTO.AuthenticationRequest;
import com.nbr.bankingsystem.DTO.AuthenticationResponse;
import com.nbr.bankingsystem.authentication.JwtTokenUtil;
import com.nbr.bankingsystem.exceptions.AuthenticationFailedException;
import com.nbr.bankingsystem.models.UserModel;
import com.nbr.bankingsystem.repositories.UserRepository;
import com.nbr.bankingsystem.services.AuthenticationService;
import com.nbr.bankingsystem.utils.AuditLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public AuthenticationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        UserModel user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    AuditLogger.log("AUTHENTICATION_FAILED", "Invalid email or password for email: " + request.getEmail());
                    return new AuthenticationFailedException("Invalid email or password.");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            AuditLogger.log("AUTHENTICATION_FAILED", "Invalid email or password for email: " + request.getEmail());
            throw new AuthenticationFailedException("Invalid email or password.");
        }

        String token = jwtTokenUtil.generateToken(user);
        AuditLogger.log("AUTHENTICATION_SUCCESS", "Token generated successfully for user: " + request.getEmail());
        return new AuthenticationResponse(token);
    }
}
