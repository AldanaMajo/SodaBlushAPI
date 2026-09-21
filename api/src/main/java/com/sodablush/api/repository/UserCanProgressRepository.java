package com.sodablush.api.repository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.sodablush.api.model.UserCanProgress;

@Repository 
public interface UserCanProgressRepository extends JpaRepository<UserCanProgress, UUID> {
    @Query("SELECT MAX(p.can.unlockOrder) FROM UserCanProgress p WHERE p.user.id = :userId AND p.status = 'COMPLETED'")
    Integer findHighestCompletedLevel(@Param("userId") UUID userId);

    java.util.Optional<UserCanProgress> findByUserIdAndCanId(UUID userId, UUID canId);

    java.util.List<UserCanProgress> findByUserId(UUID userId);

    long countByUserIdAndStatus(UUID userId, String status);

    void deleteByUserId(UUID userId);
}