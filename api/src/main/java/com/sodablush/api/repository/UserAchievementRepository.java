package com.sodablush.api.repository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sodablush.api.model.UserAchievement;

@Repository 
public interface UserAchievementRepository extends JpaRepository<UserAchievement, UUID> {
    java.util.List<UserAchievement> findByUserId(UUID userId);

    java.util.Optional<UserAchievement> findByUserIdAndAchievementId(UUID userId, UUID achievementId);

}