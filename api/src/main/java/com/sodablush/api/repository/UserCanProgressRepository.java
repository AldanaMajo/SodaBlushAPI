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
}