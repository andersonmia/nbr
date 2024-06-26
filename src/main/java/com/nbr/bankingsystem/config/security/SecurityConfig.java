package com.nbr.bankingsystem.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.nbr.bankingsystem.authentication.JwtAuthenticationFilter;
import com.nbr.bankingsystem.authentication.JwtAuthenticationProvider;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    /**
     * Constructor-based dependency injection for JwtAuthenticationFilter, JwtAuthenticationProvider, and CustomAccessDeniedHandler.
     *
     * @param jwtAuthenticationFilter     the JWT authentication filter
     * @param jwtAuthenticationProvider   the JWT authentication provider
     * @param customAccessDeniedHandler   the custom access denied handler
     */
    public SecurityConfig(@Lazy JwtAuthenticationFilter jwtAuthenticationFilter,
                          @Lazy JwtAuthenticationProvider jwtAuthenticationProvider,
                          @Lazy CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    /**
     * Bean for password encoding using BCryptPasswordEncoder.
     * BCryptPasswordEncoder is a password hashing function designed for secure password hashing.
     *
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Security filter chain configuration for HTTP security.
     * This method configures the security filter chain with JWT authentication, CSRF disabling, exception handling, and access rules.
     *
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs while configuring the security filter chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Allow access to Swagger UI and API docs without authentication
                        .requestMatchers("/users/register", "/users/login", "/customers/register").permitAll() // Allow access to user and customer registration and login without authentication
                        .requestMatchers("/transactions/**", "/customers/**").authenticated() // Require authentication for transactions and customer endpoints
                        .requestMatchers("/admin/**", "/reports/**").hasRole("ADMIN") // Restrict access to admin and reports endpoints to users with the ADMIN role
                        .anyRequest().authenticated() // Require authentication for any other requests
                )
                .csrf().disable() // Disable CSRF protection
                .exceptionHandling()
                .accessDeniedHandler(customAccessDeniedHandler) // Use custom access denied handler
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT authentication filter before the UsernamePasswordAuthenticationFilter

        return http.build();
    }
}
