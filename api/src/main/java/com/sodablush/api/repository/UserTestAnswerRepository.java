package com.sodablush.api.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sodablush.api.model.UserTestAnswer;

@Repository 
public interface UserTestAnswerRepository extends JpaRepository<UserTestAnswer, UUID> {
    java.util.List<UserTestAnswer> findByAttemptId(UUID attemptId);

    boolean existsByAttemptIdAndQuestionId(UUID attemptId, UUID questionId);

}
