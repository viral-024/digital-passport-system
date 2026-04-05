package com.passport.system.repository;

import com.passport.system.entity.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationRepository extends JpaRepository<Verification, Long> {
    List<Verification> findByOfficerId(Long officerId);
    List<Verification> findByApplicationUserId(Long userId);
    Optional<Verification> findByIdAndOfficerId(Long id, Long officerId);
    Optional<Verification> findByIdAndApplicationUserId(Long id, Long userId);
}
