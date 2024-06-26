package com.nbr.bankingsystem.repositories;

import com.nbr.bankingsystem.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Message entity.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
