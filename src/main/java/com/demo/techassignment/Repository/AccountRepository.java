package com.demo.techassignment.Repository;

import com.demo.techassignment.Model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Integer> {
    Optional<Account> findByAccountNo(String accountNo);
    Optional<Account> findByUsername(String username);
}
