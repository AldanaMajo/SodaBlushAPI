package com.sodablush.api.repository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sodablush.api.model.UserDrinkProgress;

@Repository 
public interface UserDrinkProgressRepository extends JpaRepository<UserDrinkProgress, UUID> {

}
