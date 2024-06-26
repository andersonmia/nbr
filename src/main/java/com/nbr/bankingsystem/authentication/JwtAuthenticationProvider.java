package com.nbr.bankingsystem.authentication;


import com.nbr.bankingsystem.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * This is a provider class for JWT (JSON Web Token) authentication.
 * It implements AuthenticationProvider to provide a method for authentication.
 * It is annotated with @Component to indicate that it is an autodetectable bean.
 *
 * The class contains references to JwtTokenUtil and CustomUserDetailsService, which are injected via the constructor.
 *
 * The main method of this class is authenticate, which:
 * - Extracts the JWT from the authentication credentials.
 * - Validates the JWT.
 * - If the JWT is valid, it gets the username from the JWT.
 * - Loads the user details from the CustomUserDetailsService.
 * - If the user details are not null, it authenticates the user and returns an authentication token.
 *
 * The supports method checks if the authentication class is assignable from UsernamePasswordAuthenticationToken.
 */

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationProvider(JwtTokenUtil jwtTokenUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String jwt = (String) authentication.getCredentials();
        if (jwtTokenUtil.validateToken(jwt)) {
            String username = jwtTokenUtil.getUsernameFromToken(jwt);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            if (userDetails != null) {
                return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            }
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
