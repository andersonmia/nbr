package com.nbr.bankingsystem.DTO;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.nbr.bankingsystem.validation.PastOrPresentDate;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO representing a customer in the banking system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {

    @NotBlank(message = "First name is required")
    @Pattern(regexp = "^[A-Za-z]+$", message = "First name can only contain letters")
    @Schema(description = "First name of the customer", example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Last name can only contain letters")
    @Schema(description = "Last name of the customer", example = "Doe")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "Email address of the customer", example = "john.doe@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password should be at least 6 characters long")
    @Schema(description = "Password of the customer", example = "password123")
    private String password;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^(078|079|072|073|074)\\d{7}$", message = "Mobile number should be valid")
    @Schema(description = "Mobile number of the customer", example = "0781234567")
    private String mobile;

    @NotBlank(message = "Date of birth is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date of birth should be in the format yyyy-MM-dd")
    @PastOrPresentDate(message = "Date of birth cannot be in the future")
    @Schema(description = "Date of birth of the customer", example = "1990-01-01")
    private String dob;
}
