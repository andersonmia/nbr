package com.nbr.bankingsystem.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.nbr.bankingsystem.enums.TransactionType;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entity representing a banking transaction in the system.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Banking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @NotNull(message = "Account number cannot be null")
    private String account;

    @NotNull(message = "Amount cannot be null")
    @Min(value = 0, message = "Amount must be positive")
    private Double amount;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Transaction type cannot be null")
    private TransactionType type;

    @Column(nullable = false, updatable = false)
    private LocalDateTime bankingDateTime;

    /**
     * Set the current date-time when persisting the entity.
     */
    @PrePersist
    protected void onCreate() {
        bankingDateTime = LocalDateTime.now();
    }
}
