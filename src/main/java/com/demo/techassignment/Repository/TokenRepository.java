package com.demo.techassignment.Repository;

import com.demo.techassignment.Model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query("""
    SELECT t from Token t inner join User u on t.user.username = u.username where t.user.username = :username and t.isLoggout = false 
""")
    List<Token> findAllTokenByUsername(String username);
    Optional<Token> findByToken(String token);

}
