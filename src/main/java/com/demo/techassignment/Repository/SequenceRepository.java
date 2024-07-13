package com.demo.techassignment.Repository;

import com.demo.techassignment.Model.Sequence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SequenceRepository extends JpaRepository<Sequence,Integer> {
    Optional<Sequence> findByTable(String table);
}
