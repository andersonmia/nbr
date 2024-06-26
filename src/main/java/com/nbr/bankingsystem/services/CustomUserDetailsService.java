package com.nbr.bankingsystem.services;

import com.nbr.bankingsystem.enums.Role;
import com.nbr.bankingsystem.models.CustomUserDetails;
import com.nbr.bankingsystem.models.UserModel;
import com.nbr.bankingsystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    private final UserService userService;

    public CustomUserDetailsService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserModel user = userService.findByEmail(email);
    if (user == null) {
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
    List<GrantedAuthority> userRoles = new ArrayList<>();
    //add the ROLE_ PREFIX
    userRoles.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    return new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword(), userRoles);

}
}