package com.nbr.bankingsystem.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * DTO representing a banking transaction in the system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankingDTO {

    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount cannot be negative")
    private Double amount;

    @NotBlank(message = "Transaction type is required")
    @Pattern(regexp = "SAVING|WITHDRAW", message = "Transaction type must be SAVING or WITHDRAW")
    private String type;
}