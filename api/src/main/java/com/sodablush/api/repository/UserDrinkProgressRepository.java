package com.sodablush.api.repository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sodablush.api.model.UserDrinkProgress;

@Repository 
public interface UserDrinkProgressRepository extends JpaRepository<UserDrinkProgress, UUID> {
    java.util.Optional<UserDrinkProgress> findByUserIdAndDrinkId(UUID userId, UUID drinkId);

    java.util.List<UserDrinkProgress> findByUserId(UUID userId);

    long countByUserIdAndCanIdAndStatus(UUID userId, UUID canId, String status);

    void deleteByUserId(UUID userId);

}
