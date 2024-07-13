package com.demo.techassignment.Model;

import com.demo.techassignment.Model.Enum.TrnStatus;
import com.demo.techassignment.Model.Enum.TrnType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Table(name = "transaction")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tranId", nullable = false, unique = true)
    private String tranId;

    @Column(name = "transactionDateTime", nullable = false)
    private LocalDateTime transactionDateTime;

    @Column(name = "accountFrom", nullable = false)
    private String accountFrom;

    @Column(name = "accountTo")
    private String accountTo;

    @Column(name = "amount" , nullable = false)
    private Double amount;

    @Column(name = "description" , nullable = false)
    private String description;

    @Column(name = "remarks")
    private String remarks;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

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
