package com.sodablush.api.repository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sodablush.api.model.UserCodeSubmission;

@Repository 
public interface UserCodeSubmissionRepository extends JpaRepository<UserCodeSubmission, UUID>{

}
