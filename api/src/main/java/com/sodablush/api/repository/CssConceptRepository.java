package com.sodablush.api.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sodablush.api.model.CssConcept;

@Repository 
public interface CssConceptRepository extends JpaRepository<CssConcept, UUID>{

}
