package com.nbr.bankingsystem.authentication;

import com.nbr.bankingsystem.services.CustomUserDetailsService;
import com.nbr.bankingsystem.utils.AuditLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * This is a filter class for JWT (JSON Web Token) authentication.
 * It extends OncePerRequestFilter to ensure a single execution per request dispatch.
 * It is annotated with @Component to indicate that it is an autodetectable bean.
 *
 * The class contains references to JwtTokenUtil and CustomUserDetailsService, which are injected via @Autowired.
 *
 * The main method of this class is doFilterInternal, which:
 * - Extracts the JWT from the request header.
 * - Validates the JWT.
 * - If the JWT is valid, it gets the username and user ID from the JWT.
 * - Loads the user details from the CustomUserDetailsService.
 * - If the user details are not null, it authenticates the user and sets the authentication in the SecurityContext.
 * - Logs the authentication or failure.
 * - Continues the filter chain.
 *
 * The helper method getJwtFromRequest extracts the JWT from the request header.
 */

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String jwt = getJwtFromRequest(request);

        if (jwt != null && jwtTokenUtil.validateToken(jwt)) {
            String username = jwtTokenUtil.getUsernameFromToken(jwt);
            Long userId = jwtTokenUtil.getIdFromToken(jwt);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            if (userDetails != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                AuditLogger.log("User Authenticated", "User: " + username + ", User ID: " + userId + ", Roles: " + userDetails.getAuthorities().toString());
            }
        } else {
            AuditLogger.log("JWT Validation Failed", "JWT: " + jwt);
        }
        chain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
