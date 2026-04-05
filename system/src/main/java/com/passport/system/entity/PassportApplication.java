package com.passport.system.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "passport_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class PassportApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name",nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 20)
    private LocalDate dob;

    @Column(nullable = false, length = 100)
    private String address;

    @Column(nullable = false, length = 100)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Many applications belong to one user
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"applications"})
    private User user;

    // One application has many documents
    @OneToMany(mappedBy = "application",
               cascade = CascadeType.ALL,
               orphanRemoval = true,    
               fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Document> documents = new ArrayList<>();

     // One application has one verification
    @OneToOne(mappedBy = "application",
              cascade = CascadeType.ALL,
              fetch = FetchType.LAZY)
    @JsonIgnore
    private Verification verification;

    // One application has one appointment
    @OneToOne(mappedBy = "application",
              cascade = CascadeType.ALL,
              fetch = FetchType.LAZY)
    @JsonIgnore
    private Appointment appointment;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }

}
