package com.nbr.bankingsystem.services;

import com.nbr.bankingsystem.DTO.AuthenticationRequest;
import com.nbr.bankingsystem.enums.Role;
import com.nbr.bankingsystem.models.UserModel;

public interface UserService {
    UserModel registerUser(AuthenticationRequest authenticationRequest, Role role);
    UserModel findByEmail(String email);
}
