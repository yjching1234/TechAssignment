package com.demo.techassignment.Repository;

import com.demo.techassignment.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByContact(String contact);
    Optional<User> findByEmail(String email);

}
