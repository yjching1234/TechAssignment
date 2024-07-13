package com.demo.techassignment.Model;

import com.demo.techassignment.Model.Enum.TrnStatus;
import com.demo.techassignment.Model.Enum.TrnType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tranId", nullable = false, unique = true)
    private Long tranId;

    @Column(name = "transactionDateTime", nullable = false)
    private LocalDateTime transactionDateTime;

    @Column(name = "accountFrom", nullable = false)
    private String accountFrom;

    @Column(name = "accountTo", nullable = false)
    private String accountTo;

    @Column(name = "amount" , nullable = false)
    private Double amount;

    @Column(name = "remarks",nullable = false)
    private String remarks;

    @Column(name = "transactionType", nullable = false)
    @Enumerated(EnumType.STRING)
    private TrnType transactionType;

    @Column(name = "transactionStatus")
    @Enumerated(EnumType.STRING)
    private TrnStatus transactionStatus;

    @Column(name = "updatedAt", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Column(name = "createdAt", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
