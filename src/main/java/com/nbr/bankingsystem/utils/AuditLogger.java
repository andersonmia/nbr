package com.nbr.bankingsystem.utils;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class AuditLogger {
    private static final Logger logger = LoggerFactory.getLogger(AuditLogger.class);

    public static void log(String action, String details) {
        logger.info("Audit Log - Action: {}, Details: {}", action, details);
    }
}
