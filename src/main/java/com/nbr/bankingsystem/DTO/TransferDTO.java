package com.nbr.bankingsystem.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO representing a transfer in the banking system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferDTO {

    @NotNull(message = "Receiver customer ID is required")
    private Long receiverCustomerId;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be greater than zero")
    private Double amount;
}
