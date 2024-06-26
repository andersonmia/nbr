package com.nbr.bankingsystem.repositories;

import com.nbr.bankingsystem.models.Banking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Banking entity.
 */
@Repository
public interface BankingRepository extends JpaRepository<Banking, Long> {
}
