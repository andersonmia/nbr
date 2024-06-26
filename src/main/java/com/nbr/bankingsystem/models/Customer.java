package com.nbr.bankingsystem.models;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Entity representing a customer in the banking system.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "mobile")
})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Pattern(regexp = "^[A-Za-z]+$", message = "First name can only contain letters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Last name can only contain letters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Balance cannot be null")
    @Min(value = 0, message = "Balance must be positive")
    private Double balance;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^(078|079|072|073|074)\\d{7}$", message = "Mobile number should be valid")
    private String mobile;

    @NotBlank(message = "Account number is required")
    private String account;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private Date dob;

    private LocalDateTime lastUpdateTime;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserModel userModel;

    /**
     * Set the last update time to current date-time before updating the entity.
     */
    @PreUpdate
    protected void onUpdate() {
        lastUpdateTime = LocalDateTime.now();
    }
}
