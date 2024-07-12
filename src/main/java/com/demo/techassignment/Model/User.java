package com.demo.techassignment.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Integer id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email" ,nullable = false, unique = true)
    @NonNull
    private String email;

    @Column(name = "contact", nullable = false, unique = true)
    @NonNull
    private String contact;

    @Column(name = "pass", nullable = false)
    @NonNull
    private String pass;

    @Column(name = "status" , nullable = false)
    @Enumerated(EnumType.STRING)
    @NonNull
    private Status Status;

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
