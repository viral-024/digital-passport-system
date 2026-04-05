package com.passport.system.repository;

import com.passport.system.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByApplicationId(Long applicationId);
    List<Document> findByApplicationIdAndApplicationUserId(Long applicationId, Long userId);
    List<Document> findByApplicationUserId(Long userId);
    Optional<Document> findByIdAndApplicationUserId(Long id, Long userId);
}
