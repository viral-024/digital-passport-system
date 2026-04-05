package com.passport.system.repository;

import com.passport.system.entity.PassportApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<PassportApplication, Long> {
    List<PassportApplication> findByUserId(Long userId);
    Optional<PassportApplication> findByIdAndUserId(Long id, Long userId);
    boolean existsByIdAndUserId(Long id, Long userId);
}
