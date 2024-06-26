package com.nbr.bankingsystem.services.impl;

import com.nbr.bankingsystem.DTO.AuthenticationRequest;
import com.nbr.bankingsystem.enums.Role;
import com.nbr.bankingsystem.exceptions.DuplicateEmailException;
import com.nbr.bankingsystem.exceptions.InvalidEmailException;
import com.nbr.bankingsystem.exceptions.ResourceNotFoundException;
import com.nbr.bankingsystem.models.UserModel;
import com.nbr.bankingsystem.repositories.UserRepository;
import com.nbr.bankingsystem.services.UserService;
import com.nbr.bankingsystem.utils.AuditLogger;
import com.nbr.bankingsystem.utils.ValidationUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserModel registerUser(AuthenticationRequest authenticationRequest, Role role) {
        if (!ValidationUtil.isValidEmail(authenticationRequest.getEmail())) {
            AuditLogger.log("REGISTER_USER_FAILED", "Invalid email format: " + authenticationRequest.getEmail());
            throw new InvalidEmailException("Invalid email format");
        }

        if (userRepository.existsByEmail(authenticationRequest.getEmail())) {
            AuditLogger.log("REGISTER_USER_FAILED", "Email already exists: " + authenticationRequest.getEmail());
            throw new DuplicateEmailException("Email already exists: " + authenticationRequest.getEmail());
        }

        UserModel user = new UserModel();
        user.setEmail(authenticationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(authenticationRequest.getPassword()));
        user.setRole(role);

        UserModel savedUser = userRepository.save(user);
        AuditLogger.log("REGISTER_USER", "Registered user with ID: " + savedUser.getId() + " and email: " + savedUser.getEmail());
        return savedUser;
    }


    @Override
    public UserModel findByEmail(String email) {
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    AuditLogger.log("FIND_USER_FAILED", "User not found with email: " + email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });
        AuditLogger.log("FIND_USER", "Found user with ID: " + user.getId() + " and email: " + email);
        return user;
    }
}
