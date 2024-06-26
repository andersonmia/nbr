package com.nbr.bankingsystem.utils;

import com.nbr.bankingsystem.enums.TransactionType;

/**
 * Utility class for converting strings to enum values.
 */
public class EnumConverter {

    /**
     * Converts a string to a TransactionType enum value.
     *
     * @param type the string representing the transaction type
     * @return the TransactionType enum value
     * @throws RuntimeException if the string does not match any TransactionType
     */
    public static TransactionType toTransactionType(String type) {
        try {
            return TransactionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid transaction type: " + type);
        }
    }
}
