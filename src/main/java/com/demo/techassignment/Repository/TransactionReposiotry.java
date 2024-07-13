package com.demo.techassignment.Repository;

import com.demo.techassignment.Model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionReposiotry extends JpaRepository<Transaction,Integer> {
    Optional<Transaction> findByTranId(Long trnId);
}
