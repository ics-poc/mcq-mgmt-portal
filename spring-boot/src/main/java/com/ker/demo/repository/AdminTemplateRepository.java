package com.ker.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ker.demo.entity.AdminTemplateEntity;

public interface AdminTemplateRepository extends JpaRepository<AdminTemplateEntity, Long> {
	
//    @Query("SELECT at FROM AdminTemplate at LEFT JOIN FETCH at.subjects s LEFT JOIN FETCH s.category WHERE at.adminTemplateId = :id")
//    Optional<AdminTemplate> fetchTemplateWithSubjects(@Param("id") Long id);
//    
//    @Query("SELECT a FROM AdminTemplate a LEFT JOIN FETCH a.category WHERE a.adminTemplateId = :id")
//    Optional<AdminTemplate> findWithCategories(@Param("id") Long id);
}

