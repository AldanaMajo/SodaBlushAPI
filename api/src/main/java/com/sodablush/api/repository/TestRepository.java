package com.sodablush.api.repository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sodablush.api.model.Test;

@Repository 
public interface TestRepository extends JpaRepository<Test, UUID> {
    java.util.Optional<Test> findByDrinkId(UUID drinkId);

}