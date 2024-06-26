package com.nbr.bankingsystem.DTO;

import com.nbr.bankingsystem.validation.PastOrPresentDate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

/**
 * DTO representing a customer update in the banking system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUpdateDTO {

    @Pattern(regexp = "^[A-Za-z]+$", message = "First name can only contain letters")
    @Schema(description = "First name of the customer", example = "Jane")
    private String firstName;

    @Pattern(regexp = "^[A-Za-z]+$", message = "Last name can only contain letters")
    @Schema(description = "Last name of the customer", example = "Doe")
    private String lastName;

    @Email(message = "Email should be valid")
    @Schema(description = "Email address of the customer", example = "jane.doe@example.com")
    private String email;

    @Pattern(regexp = "^(078|079|072|073|074)\\d{7}$", message = "Mobile number should be valid")
    @Schema(description = "Mobile number of the customer", example = "0791234567")
    private String mobile;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date of birth should be in the format yyyy-MM-dd")
    @PastOrPresentDate(message = "Date of birth cannot be in the future")
    @Schema(description = "Date of birth of the customer", example = "1992-02-02")
    private String dob;

    @Size(min = 6, message = "Password should be at least 6 characters long")
    @Schema(description = "Password of the customer", example = "password123")
    private String password;
}
