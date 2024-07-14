package com.demo.techassignment.Model;

import com.demo.techassignment.Model.Enum.AccStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Table(name = "account")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "accountNo", nullable = false, unique = true)
    private String accountNo;

    @Column(name = "balance", nullable = false)
    private Double balance;

    @Column(nullable = false)
    private Double tempBalance;

//    @Column(name = "expiryDate", nullable = false)
//    private Date expiryDate;

    @Column(name = "accountStatus")
    @Enumerated(EnumType.STRING)
    private AccStatus accountStatus;

    @Column(name = "remarks")
    private String remarks;

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

    public String generateAccountNo(){
        StringBuilder sb = new StringBuilder(12); // 15 digits for the account number without hyphens
        Random random = new Random();
        for (int i = 0; i < 17; i++) { // 15 digits - 3 hyphens
            sb.append(random.nextInt(10));
        }

        return formatAccNo(sb.toString());
    }

    public static String formatAccNo(String input) {
        // Ensure the string is 16 characters long by padding with zeros
//        input = String.format("%-16s", input).replace(' ', '0');

        // Format the string
        String formatted = input.substring(0, 4) + "-" +
                input.substring(4, 8) + "-" +
                input.substring(8, 12) + "-" +
                input.substring(12, 16);

        return formatted;
    }
}
