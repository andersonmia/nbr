package com.nbr.bankingsystem.authentication;


import com.nbr.bankingsystem.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * This is the configuration class for the authentication manager in the application.
 * It is annotated with @Configuration to indicate that it is a source of bean definitions.
 * The class contains a reference to the custom user details service, JWT authentication provider, and password encoder.
 * These are injected via the constructor.
 *
 * The class provides a bean of type AuthenticationManager which is configured with the custom user details service,
 * password encoder, and JWT authentication provider.
 */

@Configuration
public class AuthenticationManagerConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationManagerConfig(CustomUserDetailsService customUserDetailsService, JwtAuthenticationProvider jwtAuthenticationProvider, PasswordEncoder passwordEncoder) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
        auth.authenticationProvider(jwtAuthenticationProvider);
        return auth.build();
    }
}