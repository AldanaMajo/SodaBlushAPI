package com.sodablush.api.repository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sodablush.api.model.UserTestAttempt;

@Repository 
public interface UserTestAttemptRepository extends JpaRepository<UserTestAttempt, UUID> {
    long countByUserIdAndTestIdAndCompletedAtIsNotNull(UUID userId, UUID testId);

    boolean existsByUserIdAndTestIdAndPassedIsTrue(UUID userId, UUID testId);

    Optional<UserTestAttempt> findFirstByUserIdAndTestIdAndCompletedAtIsNullOrderByStartedAtDesc(
            UUID userId, UUID testId);

    void deleteByUserId(UUID userId);
}
