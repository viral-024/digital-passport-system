package com.passport.system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "verifications")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Verification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VerificationStatus status;

    @Column(length = 255)
    private String remarks;

    @Column(name = "verified_at", nullable = false, updatable = false)
    private LocalDateTime verifiedAt;

    // One verification belongs to one application
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"documents", "user", "verification", "appointment"})
    private PassportApplication application;

    // Many verifications can be done by one officer
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "officer_id", nullable = false)
    @JsonIgnoreProperties({"applications"})
    private User officer;

    @PrePersist
    protected void onVerify() {
        this.verifiedAt = LocalDateTime.now();
    }
}