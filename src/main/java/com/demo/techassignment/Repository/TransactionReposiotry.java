package com.demo.techassignment.Repository;

import com.demo.techassignment.DTO.TrnHistoryDTO;
import com.demo.techassignment.Model.Enum.TrnStatus;
import com.demo.techassignment.Model.Enum.TrnType;
import com.demo.techassignment.Model.Transaction;
import com.demo.techassignment.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface TransactionReposiotry extends JpaRepository<Transaction,Integer> {
    Optional<Transaction> findByTranId(String trnId);

    @Query("""
    SELECT t FROM Transaction t WHERE
    (:userId IS NULL or t.user.id = :userId)
    AND (:trnId IS NULL OR t.tranId = :trnId)
    AND (:trnStatus IS NULL OR t.transactionStatus = :trnStatus)
    AND (:trnType IS NULL OR t.transactionType = :trnType)
    AND (:dateFrom IS NULL OR t.transactionDateTime >= :dateFrom)
    AND (:dateTo IS NULL OR t.transactionDateTime <= :dateTo)
    """)
    Page<Transaction> findTransactionByFilters(
            @Param("userId") Integer userId,
            @Param("trnId") String trnId,
            @Param("trnStatus") TrnStatus trnStatus,
            @Param("trnType") TrnType trnType,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            Pageable pageable);
}
